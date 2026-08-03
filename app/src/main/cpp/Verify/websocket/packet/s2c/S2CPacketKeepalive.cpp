#include "websocket/packet/s2c/S2CPacketKeepalive.h"
#include "util/DateTimeUtil.h"
#include "../../../../Utils/Logger.h"
#include "Guard/FrameCounter.h"
#include "Guard/RenderConfig.h"
#include <set>

namespace team::cool::client::websocket::packet::s2c {

std::set<double> S2CPacketKeepalive::ids;
int64_t S2CPacketKeepalive::lastActive = 0;

void S2CPacketKeepalive::read(ByteBuf& buf) {
    id = buf.readDouble();
}

void S2CPacketKeepalive::handle() {
    LOGI("[TIME] 收到心跳包: id=%.2f", id);

    if (ids.find(id) != ids.end()) {
        // Replay attack detected
        LOGW("[TIME] 检测到重放攻击: id=%.2f 已使用!", id);
        return;
    }

    ids.insert(id);
    
    // 防止内存无限增长：保留最近100个心跳ID
    constexpr size_t MAX_IDS = 100;
    if (ids.size() > MAX_IDS) {
        // 清理最旧的一半 (set是有序的，最小值最旧)
        auto it = ids.begin();
        std::advance(it, ids.size() / 2);
        ids.erase(ids.begin(), it);
        LOGD("[TIME] 清理旧心跳ID，当前数量: %zu", ids.size());
    }
    
    lastActive = util::DateTimeUtil::getTime();

    LOGI("[TIME] 心跳有效，重置会话时间");

    // Reset time decay on valid heartbeat from server
    // This is the key: only server can extend session lifetime
    guard::FrameCounter::startSession();

    // Optionally refresh seed validation timestamp
    uint32_t currentSeed = guard::RenderConfig::getTextureSampling();
    if (currentSeed % 23 == 0) {
        // Re-apply same seed to refresh timestamp
        guard::RenderConfig::updateTextureConfig(currentSeed);
        LOGD("[TIME] 种子时间戳已刷新");
    }

    LOGI("[TIME] 会话时间已重置");
}

void S2CPacketKeepalive::reset() {
    lastActive = util::DateTimeUtil::getTime();
}

} // namespace team::cool::client::websocket::packet::s2c

