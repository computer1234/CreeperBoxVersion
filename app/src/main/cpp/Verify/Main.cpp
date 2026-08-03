#include "Main.h"
#include "MacroFix.h"
#include "Config.h"
#include "Guard/RenderConfig.h"
#include "Guard/FrameCounter.h"
#include "Guard/EnvironmentScanner.h"
#include "util/DateTimeUtil.h"
#include "../Utils/Logger.h"
#include "util/crypt/SecureRandom.h"
#include "websocket/handler/LoginHandler.h"
#include "websocket/handler/PacketHandler.h"
#include "websocket/handler/MessageHandler.h"
#include <climits>
#include <openssl/crypto.h>
#include <atomic>
#include <thread>
#include <openssl/provider.h>
#include <openssl/conf.h>
#include <fcntl.h>
#ifdef _WIN32
#include <io.h>
#endif

namespace team::cool::client {

// Internal render state (obfuscated)
std::atomic<int> Main::m_renderState{1};
VerifyStatus Main::status = VerifyStatus::NOT_STARTED;
std::string Main::lastError = "";
std::map<std::string, std::string> Main::inGameNames;

// Decoy variables (honeypots - modifying has no effect)
int Main::verifyCode = 0;
bool Main::licenseValid = false;

const std::string Main::IP = "218.93.206.141";
const int Main::PORT = 8081;
const std::string Main::URL = Main::IP + ":" + std::to_string(Main::PORT);

void* Main::loginHandler = nullptr;
std::vector<StatusCallback> Main::callbacks;

static std::atomic<bool> running{true};
static std::mutex statusMutex;
static std::mutex callbackMutex;

void signalHandler(int signal) {
    LOGI("正在关闭程序...");
    running = false;
    
    if (Main::loginHandler) {
        auto* loginHandler = static_cast<websocket::handler::LoginHandler*>(Main::loginHandler);
        if (loginHandler) {
            loginHandler->close(1000, "程序退出");
        }
    }
    
    // Close message handler if exists
    auto* msgHandler = websocket::handler::MessageHandler::getInstance();
    if (msgHandler) {
        msgHandler->close();
    }
    
    LOGI("程序关闭成功！");
    std::exit(0);
}

void init_openssl_providers() {
    if (!OSSL_PROVIDER_load(NULL, "default")) {
        LOGW("Failed to load default provider!");
    }

    if (!OSSL_PROVIDER_load(NULL, "base")) {
        LOGW("Failed to load base provider!");
    }

    if (!OSSL_PROVIDER_load(NULL, "legacy")) {
        LOGW("Failed to load legacy provider!");
    }
}

void Main::startVerify() {
    setStatus(VerifyStatus::CONNECTING);
    
    // 启动验证流程（在后台线程运行）
    std::thread verifyThread([]() {
        init_openssl_providers();

        int64_t localTime = util::DateTimeUtil::systemTime();
        int64_t serverTime = util::DateTimeUtil::getTime();
        LOGI("时间戳校验: 本地时间=%lld, 服务器时间=%lld, 差值=%lld ms", 
             static_cast<long long>(localTime), 
             static_cast<long long>(serverTime),
             static_cast<long long>(std::abs(localTime - serverTime)));
        
        if (util::DateTimeUtil::expiry(localTime)) {
            LOGE("时间戳效验失败: 本地=%lld, 服务器=%lld, 差值=%lld ms (超过5分钟)", 
                 static_cast<long long>(localTime), 
                 static_cast<long long>(serverTime),
                 static_cast<long long>(std::abs(localTime - serverTime)));
            setStatus(VerifyStatus::FAILED_TIMEOUT, "时间戳效验失败");
            return;
        }
        
        websocket::handler::PacketHandler::init();
        
        LOGI("验证模块初始化完成");
        
        // 创建静态handler避免局部变量生命周期问题
        static websocket::handler::LoginHandler handler("ws://" + URL + "/login");
        Main::loginHandler = &handler;
        
        // 设置连接回调
        handler.onError([](const std::string& error) {
            setStatus(VerifyStatus::FAILED_CONNECT, error);
        });
        
        handler.onClose([](int code, const std::string& reason) {
            if (status != VerifyStatus::SUCCESS) {
                setStatus(VerifyStatus::DISCONNECTED, reason);
            }
        });
        
        handler.connect();
        
        // 保持线程运行
        while (running) {
            std::this_thread::sleep_for(std::chrono::milliseconds(100));
        }
    });
    verifyThread.detach();
}

// Decoy function - HONEYPOT (modifying return value has no effect)
bool Main::checkLicense() {
    // This is a trap - always returns false
    // Real validation is in RenderConfig::validateRenderingConfig()
    return licenseValid && (verifyCode == 12345);
}

// Decoy function - HONEYPOT (modifying return value has no effect)
bool Main::isActivated() {
    // This is a trap - always returns true to mislead crackers
    // They might think patching this to true enables features
    return true;
}

// Decoy function - HONEYPOT
int Main::getLicenseCode() {
    // Returns garbage value - not used anywhere
    return verifyCode ^ 0xDEAD;
}

bool Main::performSecurityCheck() {
    // 1. Time Decay Check (FrameCounter)
    bool timeCheck = guard::FrameCounter::shouldContinueRendering();
    
    // 2. Four Seeds Mathematical Lock (RenderConfig)
    bool seedCheck = guard::RenderConfig::validateRenderingConfig();
    
    // 3. Environment Integrity (Scanner)
    bool envCheck = guard::EnvironmentScanner::isEnvironmentClean();

    // Log failure for debugging (obfuscate in production)
    if (!timeCheck || !seedCheck || !envCheck) {
        static int logCounter = 0;
        if (logCounter++ % 100 == 0) {
           // LOGW("Security Check Failed: Time=%d, Seed=%d, Env=%d", timeCheck, seedCheck, envCheck);
        }
        return false;
    }
    return true;
}

bool Main::waitForVerification(int timeoutMs, int pollIntervalMs) {
    int elapsed = 0;
    while (elapsed < timeoutMs) {
        // Check distributed validation (real check)
        if (guard::RenderConfig::validateRenderingConfig() &&
            guard::FrameCounter::isSessionActive()) {
            LOGI("Connection ready: %dms", elapsed);
            return true;
        }

        // Check if failed
        if (isFailed()) {
            LOGE("Connection failed: %s - %s", getStatusString().c_str(), getLastError().c_str());
            return false;
        }

        std::this_thread::sleep_for(std::chrono::milliseconds(pollIntervalMs));
        elapsed += pollIntervalMs;
    }
    setStatus(VerifyStatus::FAILED_TIMEOUT, "Timeout");
    LOGE("Connection timeout: %dms", timeoutMs);
    return false;
}

void Main::setCredentials(const std::string& username, 
                          const std::string& password,
                          const std::string& hwid) {
    Config::setUsername(username);
    Config::setPassword(password);
    Config::setHardwareId(hwid);
}

// 状态管理方法实现
VerifyStatus Main::getStatus() {
    std::lock_guard<std::mutex> lock(statusMutex);
    return status;
}

std::string Main::getStatusString() {
    std::lock_guard<std::mutex> lock(statusMutex);
    switch (status) {
        case VerifyStatus::NOT_STARTED:      return "未开始";
        case VerifyStatus::CONNECTING:       return "正在连接服务器";
        case VerifyStatus::AUTHENTICATING:   return "正在验证身份";
        case VerifyStatus::SUCCESS:          return "验证成功";
        case VerifyStatus::FAILED_AUTH:      return "验证失败（用户名/密码错误）";
        case VerifyStatus::FAILED_CONNECT:   return "连接失败";
        case VerifyStatus::FAILED_TIMEOUT:   return "连接超时";
        case VerifyStatus::FAILED_SIGNATURE: return "签名验证失败";
        case VerifyStatus::DISCONNECTED:     return "连接已断开";
        case VerifyStatus::HEARTBEAT_TIMEOUT:return "心跳超时";
        default:                             return "未知状态";
    }
}

std::string Main::getLastError() {
    std::lock_guard<std::mutex> lock(statusMutex);
    return lastError;
}

bool Main::isFailed() {
    std::lock_guard<std::mutex> lock(statusMutex);
    return status == VerifyStatus::FAILED_AUTH ||
           status == VerifyStatus::FAILED_CONNECT ||
           status == VerifyStatus::FAILED_TIMEOUT ||
           status == VerifyStatus::FAILED_SIGNATURE ||
           status == VerifyStatus::DISCONNECTED ||
           status == VerifyStatus::HEARTBEAT_TIMEOUT;
}

void Main::setStatus(VerifyStatus newStatus, const std::string& error) {
    {
        std::lock_guard<std::mutex> lock(statusMutex);
        status = newStatus;
        if (!error.empty()) {
            lastError = error;
        }
    }
    
    // 打印日志
    if (!error.empty()) {
        LOGI("[验证状态] %s - %s", getStatusString().c_str(), error.c_str());
    } else {
        LOGI("[验证状态] %s", getStatusString().c_str());
    }
    
    // 触发回调
    notifyCallbacks();
}

void Main::onStatusChanged(StatusCallback callback) {
    std::lock_guard<std::mutex> lock(callbackMutex);
    callbacks.push_back(std::move(callback));
}

void Main::clearCallbacks() {
    std::lock_guard<std::mutex> lock(callbackMutex);
    callbacks.clear();
}

void Main::notifyCallbacks() {
    std::vector<StatusCallback> callbacksCopy;
    VerifyStatus currentStatus;
    std::string currentError;
    
    // 复制回调列表和状态，避免长时间持有锁
    {
        std::lock_guard<std::mutex> lock(callbackMutex);
        callbacksCopy = callbacks;
    }
    {
        std::lock_guard<std::mutex> lock(statusMutex);
        currentStatus = status;
        currentError = lastError;
    }
    
    // 在锁外执行回调
    for (const auto& cb : callbacksCopy) {
        try {
            cb(currentStatus, currentError);
        } catch (const std::exception& e) {
            LOGE("回调执行异常: %s", e.what());
        }
    }
}

} // namespace team::cool::client
