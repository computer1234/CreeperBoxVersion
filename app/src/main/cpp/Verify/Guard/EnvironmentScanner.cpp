#include "EnvironmentScanner.h"
#include "../MacroFix.h"
#include "RenderConfig.h"
#include "../../Utils/Logger.h"
#include <chrono>
#include <fstream>
#include <sstream>
#include <cstring>
#include <dirent.h>
#include <unistd.h>
#include <sys/ptrace.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/stat.h>
#include <dlfcn.h>

namespace team::cool::client::guard {

// Static member definitions
std::atomic<bool> EnvironmentScanner::m_scanComplete{false};
std::atomic<bool> EnvironmentScanner::m_lastScanResult{false};
std::atomic<int64_t> EnvironmentScanner::m_lastScanTime{0};

int64_t EnvironmentScanner::getCurrentTimeMs() {
    auto now = std::chrono::steady_clock::now();
    return std::chrono::duration_cast<std::chrono::milliseconds>(
        now.time_since_epoch()
    ).count();
}

bool EnvironmentScanner::readFileContent(const std::string& path, std::string& content) {
    std::ifstream file(path);
    if (!file.is_open()) {
        return false;
    }

    std::stringstream buffer;
    buffer << file.rdbuf();
    content = buffer.str();
    return true;
}

bool EnvironmentScanner::checkPortOpen(int port) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) return false;

    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);

    // Set non-blocking with timeout
    struct timeval timeout;
    timeout.tv_sec = 0;
    timeout.tv_usec = 100000;  // 100ms
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));

    int result = connect(sock, (struct sockaddr*)&addr, sizeof(addr));
    close(sock);

    return result == 0;
}

bool EnvironmentScanner::detectTracerAttachment() {
    // Method 1: Check /proc/self/status for TracerPid
    std::string status;
    if (readFileContent("/proc/self/status", status)) {
        size_t pos = status.find("TracerPid:");
        if (pos != std::string::npos) {
            pos += 10;  // Skip "TracerPid:"
            while (pos < status.length() && (status[pos] == ' ' || status[pos] == '\t')) {
                pos++;
            }
            if (pos < status.length() && status[pos] != '0') {
                return true;  // TracerPid is non-zero
            }
        }
    }

    return false;
}

bool EnvironmentScanner::detectTimingAnomalies() {
    // Check for time going backwards (VM snapshot restore)
    static int64_t lastCheck = 0;
    int64_t currentTime = getCurrentTimeMs();
    if (lastCheck > 0 && currentTime < lastCheck) {
        return true;  // Time went backwards
    }
    lastCheck = currentTime;

    return false;
}

bool EnvironmentScanner::detectBreakpointInstructions() {
    // Look for Frida gadget or similar
    void* handle = dlopen(nullptr, RTLD_NOW);
    if (handle) {
        // Check for known Frida symbols
        if (dlsym(handle, "frida_agent_main") != nullptr) {
            return true;
        }
        if (dlsym(handle, "gum_interceptor_obtain") != nullptr) {
            return true;
        }
    }

    return false;
}

bool EnvironmentScanner::detectVirtualizationEnvironment() {
    // Check for common emulator/VM indicators on Android
    struct stat st;
    if (stat("/dev/qemu_pipe", &st) == 0) {
        return true;
    }
    if (stat("/dev/goldfish_pipe", &st) == 0) {
        return true;
    }

    return false;
}

bool EnvironmentScanner::detectInstrumentationFramework() {
    // Check for Frida server ports
    if (checkPortOpen(27042) || checkPortOpen(27043)) {
        return true;
    }

    // Check for Frida server process
    DIR* dir = opendir("/data/local/tmp");
    if (dir) {
        struct dirent* entry;
        while ((entry = readdir(dir)) != nullptr) {
            if (strstr(entry->d_name, "frida") != nullptr ||
                strstr(entry->d_name, "gadget") != nullptr) {
                closedir(dir);
                return true;
            }
        }
        closedir(dir);
    }

    return false;
}

void EnvironmentScanner::initializeScanner() {
    m_scanComplete.store(false, std::memory_order_release);
    m_lastScanResult.store(false, std::memory_order_release);
    m_lastScanTime.store(0, std::memory_order_release);
}

bool EnvironmentScanner::performFullScan() {
    // Rate limit scans to reduce overhead
    int64_t currentTime = getCurrentTimeMs();
    int64_t lastScan = m_lastScanTime.load(std::memory_order_acquire);

    if (lastScan > 0 && (currentTime - lastScan) < SCAN_INTERVAL_MS) {
        // Return cached result
        return m_lastScanResult.load(std::memory_order_acquire);
    }

    // Perform all checks
    bool clean = true;

    if (detectTracerAttachment()) {
        LOGW("[ENV] FAIL: TracerPid检测到调试器!");
        clean = false;
    }

    if (clean && detectTimingAnomalies()) {
        LOGW("[ENV] FAIL: 时序异常检测到异常!");
        clean = false;
    }

    if (clean && detectBreakpointInstructions()) {
        LOGW("[ENV] FAIL: 断点/Hook检测到异常!");
        clean = false;
    }

    if (clean && detectVirtualizationEnvironment()) {
        LOGW("[ENV] FAIL: 虚拟化检测到VM/模拟器!");
        clean = false;
    }

    if (clean && detectInstrumentationFramework()) {
        LOGW("[ENV] FAIL: Frida/Xposed检测到插桩框架!");
        clean = false;
    }

    // Cache results
    m_lastScanResult.store(clean, std::memory_order_release);
    m_lastScanTime.store(currentTime, std::memory_order_release);
    m_scanComplete.store(true, std::memory_order_release);

    return clean;
}

bool EnvironmentScanner::isEnvironmentClean() {
    // Quick cached check
    if (m_scanComplete.load(std::memory_order_acquire)) {
        int64_t lastScan = m_lastScanTime.load(std::memory_order_acquire);
        int64_t currentTime = getCurrentTimeMs();

        if ((currentTime - lastScan) < SCAN_INTERVAL_MS) {
            return m_lastScanResult.load(std::memory_order_acquire);
        }
    }

    // Need fresh scan
    return performFullScan();
}

bool EnvironmentScanner::checkTracerStatus() {
    return !detectTracerAttachment();
}

bool EnvironmentScanner::checkTimingIntegrity() {
    return !detectTimingAnomalies();
}

bool EnvironmentScanner::checkCodeIntegrity() {
    return !detectBreakpointInstructions();
}

bool EnvironmentScanner::checkVirtualization() {
    return !detectVirtualizationEnvironment();
}

bool EnvironmentScanner::checkInstrumentation() {
    return !detectInstrumentationFramework();
}

int EnvironmentScanner::getSecurityLevel() {
    // Use cached result to avoid re-running all detections
    if (!m_lastScanResult.load(std::memory_order_acquire)) {
        return 0;
    }
    return 100;
}

uint32_t EnvironmentScanner::getEnvironmentFactor() {
    if (!isEnvironmentClean()) {
        return 0;
    }

    uint32_t configFactor = RenderConfig::getConfigurationFactor();
    uint32_t securityLevel = static_cast<uint32_t>(getSecurityLevel());

    return (configFactor ^ (securityLevel * 0x9E3779B1)) | 1;
}

} // namespace team::cool::client::guard
