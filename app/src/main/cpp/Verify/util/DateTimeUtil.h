#pragma once

#include <cstdint>

namespace team::cool::client::util {

class DateTimeUtil {
private:
    static constexpr int64_t MAX_PING = 1000LL * 60 * 5; // 5 分钟
    static int networkFailureCount;
public:
    static constexpr int64_t KEEPALIVE = 60 * 1000;

    static bool expiry(int64_t packetTime);
    static bool expiry(int64_t packetTime, int64_t expiryTime);
    static int64_t getTime();
    static int64_t systemTime();
};

} // namespace team::cool::client::util

