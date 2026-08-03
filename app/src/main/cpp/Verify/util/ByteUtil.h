#pragma once

#include <vector>
#include <cstdint>
#include <iostream>

namespace team::cool::client::util {

static const char* HEX_LO = "0123456789abcdef";
static const char* HEX_UP = "0123456789ABCDEF";

class ByteUtil {
public:
    static std::vector<uint8_t> merge(const std::vector<uint8_t>& a, const std::vector<uint8_t>& b);
    static std::pair<std::vector<uint8_t>, std::vector<uint8_t>> split(const std::vector<uint8_t>& merged, size_t aLength);
    static std::vector<uint8_t> longToBytes(std::int64_t value);
    static std::int64_t bytesToLong(const std::vector<uint8_t>& bytes);
    static std::string bytesToHex(const uint8_t* data, size_t len, bool upper = false);
    static std::string bytesToHex(const std::vector<uint8_t>& v, bool upper = false);
};

} // namespace team::cool::client::util

