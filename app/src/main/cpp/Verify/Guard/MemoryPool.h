#pragma once

#include <cstdint>
#include <vector>
#include <atomic>
#include <mutex>

namespace team::cool::client::guard {

/**
 * Memory pool allocation manager
 * Manages memory block allocations with integrity tracking
 */
class MemoryPool {
private:
    struct BlockInfo {
        void* address;
        size_t size;
        uint32_t checksum;
    };

    static std::vector<BlockInfo> m_allocations;
    static std::mutex m_poolMutex;
    static std::atomic<bool> m_initialized;
    static std::atomic<uint32_t> m_poolChecksum;

    // CRC32 computation
    static uint32_t calculateCRC32(const void* data, size_t length);
    static uint32_t computeBlockHash(void* addr, size_t len);

public:
    // Initialization
    static void initializePool();
    static void destroyPool();

    // Registration of critical memory regions
    static void registerBlock(void* address, size_t size);
    static void unregisterBlock(void* address);

    // Integrity verification
    static bool verifyAllBlocks();
    static bool verifyBlock(void* address);
    static bool checkPoolIntegrity();

    // Memory statistics
    static size_t getRegisteredBlockCount();
    static size_t getTotalRegisteredSize();

    // Derived integrity value for cross-module checks
    static uint32_t getIntegrityFactor();
};

} // namespace team::cool::client::guard
