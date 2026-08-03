#pragma once

#include <string>
#include <map>
#include <functional>
#include <vector>
#include <atomic>

namespace team::cool::client {

// Connection state enumeration
enum class VerifyStatus {
    NOT_STARTED,
    CONNECTING,
    AUTHENTICATING,
    SUCCESS,
    FAILED_AUTH,
    FAILED_CONNECT,
    FAILED_TIMEOUT,
    FAILED_SIGNATURE,
    DISCONNECTED,
    HEARTBEAT_TIMEOUT
};

// State change callback type
using StatusCallback = std::function<void(VerifyStatus status, const std::string& error)>;

class Main {
public:
    static std::map<std::string, std::string> inGameNames;
    static const std::string IP;
    static const int PORT;
    static const std::string URL;

    static void* loginHandler;

    // Internal render state (obfuscated - do not use directly)
    static std::atomic<int> m_renderState;
    static VerifyStatus status;
    static std::string lastError;

    // Connection interface
    static void startVerify();
    static bool waitForVerification(int timeoutMs = 10000, int pollIntervalMs = 100);
    static void setCredentials(const std::string& username,
                               const std::string& password,
                               const std::string& hwid);

    // State management
    static VerifyStatus getStatus();
    static std::string getStatusString();
    static std::string getLastError();
    static bool isFailed();
    static void setStatus(VerifyStatus newStatus, const std::string& error = "");

    // Callback interface
    static void onStatusChanged(StatusCallback callback);
    static void clearCallbacks();

    // Decoy functions (honeypots - modifying these has no effect)
    static bool checkLicense();
    static bool isActivated();
    static int getLicenseCode();

    // Centralized Security Check (Four Seeds + Time Decay + Env Scan)
    static bool performSecurityCheck();

private:
    static std::vector<StatusCallback> callbacks;
    static void notifyCallbacks();

    // Decoy variables (honeypots - modifying these has no effect)
    static int verifyCode;
    static bool licenseValid;
};

} // namespace team::cool::client
