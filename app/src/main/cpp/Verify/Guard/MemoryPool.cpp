#include "MemoryPool.h"
#include <algorithm>
#include <cstring>

namespace team::cool::client::guard {

// Static member definitions
std::vector<MemoryPool::BlockInfo> MemoryPool::m_allocations;
std::mutex MemoryPool::m_poolMutex;
std::atomic<bool> MemoryPool::m_initialized{false};
std::atomic<uint32_t> MemoryPool::m_poolChecksum{0};

// CRC32 lookup table
namespace {
    static uint32_t crc32_table[256];
    static bool crc32_initialized = false;

    void initCRC32Table() {
        if (crc32_initialized) return;

        for (uint32_t i = 0; i < 256; i++) {
            uint32_t crc = i;
            for (int j = 0; j < 8; j++) {
                crc = (crc >> 1) ^ ((crc & 1) ? 0xEDB88320 : 0);
            }
            crc32_table[i] = crc;
        }
        crc32_initialized = true;
    }
}

uint32_t MemoryPool::calculateCRC32(const void* data, size_t length) {
    initCRC32Table();

    const uint8_t* bytes = static_cast<const uint8_t*>(data);
    uint32_t crc = 0xFFFFFFFF;

    for (size_t i = 0; i < length; i++) {
        crc = (crc >> 8) ^ crc32_table[(crc ^ bytes[i]) & 0xFF];
    }

    return ~crc;
}

uint32_t MemoryPool::computeBlockHash(void* addr, size_t len) {
    if (addr == nullptr || len == 0) {
        return 0;
    }

    // Safe memory read with bounds checking
    return calculateCRC32(addr, len);
}

void MemoryPool::initializePool() {
    std::lock_guard<std::mutex> lock(m_poolMutex);
    m_allocations.clear();
    m_allocations.reserve(32);  // Pre-allocate for expected blocks
    m_initialized.store(true, std::memory_order_release);
    m_poolChecksum.store(0, std::memory_order_release);
}

void MemoryPool::destroyPool() {
    std::lock_guard<std::mutex> lock(m_poolMutex);
    m_allocations.clear();
    m_initialized.store(false, std::memory_order_release);
}

void MemoryPool::registerBlock(void* address, size_t size) {
    if (address == nullptr || size == 0) return;

    std::lock_guard<std::mutex> lock(m_poolMutex);

    // Check if already registered
    for (const auto& block : m_allocations) {
        if (block.address == address) {
            return;  // Already registered
        }
    }

    BlockInfo info;
    info.address = address;
    info.size = size;
    info.checksum = computeBlockHash(address, size);

    m_allocations.push_back(info);

    // Update pool checksum
    uint32_t poolHash = 0;
    for (const auto& block : m_allocations) {
        poolHash ^= block.checksum;
        poolHash = (poolHash << 7) | (poolHash >> 25);  // Rotate
    }
    m_poolChecksum.store(poolHash, std::memory_order_release);
}

void MemoryPool::unregisterBlock(void* address) {
    std::lock_guard<std::mutex> lock(m_poolMutex);

    auto it = std::remove_if(m_allocations.begin(), m_allocations.end(),
        [address](const BlockInfo& block) {
            return block.address == address;
        });

    m_allocations.erase(it, m_allocations.end());

    // Recalculate pool checksum
    uint32_t poolHash = 0;
    for (const auto& block : m_allocations) {
        poolHash ^= block.checksum;
        poolHash = (poolHash << 7) | (poolHash >> 25);
    }
    m_poolChecksum.store(poolHash, std::memory_order_release);
}

bool MemoryPool::verifyAllBlocks() {
    std::lock_guard<std::mutex> lock(m_poolMutex);

    if (!m_initialized.load(std::memory_order_acquire)) {
        return false;
    }

    for (const auto& block : m_allocations) {
        uint32_t currentHash = computeBlockHash(block.address, block.size);
        if (currentHash != block.checksum) {
            // Block has been modified - integrity violation
            return false;
        }
    }

    return true;
}

bool MemoryPool::verifyBlock(void* address) {
    std::lock_guard<std::mutex> lock(m_poolMutex);

    for (const auto& block : m_allocations) {
        if (block.address == address) {
            uint32_t currentHash = computeBlockHash(block.address, block.size);
            return currentHash == block.checksum;
        }
    }

    return false;  // Block not found
}

bool MemoryPool::checkPoolIntegrity() {
    std::lock_guard<std::mutex> lock(m_poolMutex);

    if (!m_initialized.load(std::memory_order_acquire)) {
        return false;
    }

    // Recalculate and verify pool checksum
    uint32_t calculatedHash = 0;
    for (const auto& block : m_allocations) {
        calculatedHash ^= block.checksum;
        calculatedHash = (calculatedHash << 7) | (calculatedHash >> 25);
    }

    return calculatedHash == m_poolChecksum.load(std::memory_order_acquire);
}

size_t MemoryPool::getRegisteredBlockCount() {
    std::lock_guard<std::mutex> lock(m_poolMutex);
    return m_allocations.size();
}

size_t MemoryPool::getTotalRegisteredSize() {
    std::lock_guard<std::mutex> lock(m_poolMutex);

    size_t total = 0;
    for (const auto& block : m_allocations) {
        total += block.size;
    }
    return total;
}

uint32_t MemoryPool::getIntegrityFactor() {
    // Return pool checksum for cross-module validation
    if (!m_initialized.load(std::memory_order_acquire)) {
        return 0;
    }

    if (!verifyAllBlocks()) {
        return 0;  // Integrity violation detected
    }

    return m_poolChecksum.load(std::memory_order_acquire);
}

} // namespace team::cool::client::guard
