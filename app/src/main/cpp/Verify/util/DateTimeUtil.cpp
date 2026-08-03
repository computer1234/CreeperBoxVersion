#include "util/DateTimeUtil.h"
#include "util/Generator.h"
#include "util/Http.h"
#include "util/crypt/RSA.h"
#include "Main.h"
#include "Config.h"
#include <regex>
#include <sstream>
#include <stdexcept>
#include <chrono>

namespace team::cool::client::util {

int DateTimeUtil::networkFailureCount = 0;

bool DateTimeUtil::expiry(int64_t packetTime) {
    return expiry(packetTime, MAX_PING);
}

bool DateTimeUtil::expiry(int64_t packetTime, int64_t expiryTime) {
    return std::abs(packetTime - getTime()) > expiryTime;
}

int64_t DateTimeUtil::systemTime() {
    return std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::system_clock::now().time_since_epoch()
    ).count();
}

int64_t DateTimeUtil::getTime() {
    if (networkFailureCount >= 3) {
        fprintf(stderr, "[DateTimeUtil] 网络失败次数已达%d次，使用本地时间\n", networkFailureCount);
        return systemTime();
    }

    const std::string nonce = Generator::nonce();
    const std::string url = "http://" + Main::URL + "/api/time/get";
    const std::string cookie = "_=" + Config::getCookie() + "; __=" + nonce;
    
    fprintf(stderr, "[DateTimeUtil] 请求时间戳: URL=%s, Cookie长度=%zu, Nonce=%s\n", 
            url.c_str(), Config::getCookie().length(), nonce.c_str());

    try {
        const std::string rawResponse = Http::post(url, cookie);
        fprintf(stderr, "[DateTimeUtil] HTTP响应长度: %zu\n", rawResponse.length());
        
        const std::string data = crypt::RSA::decryptFromBase64(rawResponse);
        fprintf(stderr, "[DateTimeUtil] 解密数据: %s\n", data.c_str());

        size_t pos = data.find('|');
        if (pos == std::string::npos) {
            fprintf(stderr, "[DateTimeUtil] 错误: 响应格式无效，缺少分隔符'|'\n");
            return -10000000;
        }
        
        std::string timeStr = data.substr(0, pos);
        std::string nonceStr = data.substr(pos + 1);
        
        fprintf(stderr, "[DateTimeUtil] 解析结果: 时间=%s, 服务器Nonce=%s, 本地Nonce=%s\n", 
                timeStr.c_str(), nonceStr.c_str(), nonce.c_str());
        
        if (nonceStr != nonce) {
            fprintf(stderr, "[DateTimeUtil] 错误: Nonce不匹配!\n");
            return -10000000;
        }
        
        int64_t serverTime = std::stoll(timeStr);
        fprintf(stderr, "[DateTimeUtil] 成功获取服务器时间: %lld\n", static_cast<long long>(serverTime));
        return serverTime;
    } catch (const std::exception &e) {
        networkFailureCount++;
        fprintf(stderr, "[DateTimeUtil] 异常(第%d次): %s\n", networkFailureCount, e.what());
        return getTime();
    }
}

} // namespace team::cool::client::util

