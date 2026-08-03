#include "util/ByteUtil.h"
#include <algorithm>
#include <sstream>

namespace team::cool::client::util {
std::vector<uint8_t> ByteUtil::merge(const std::vector<uint8_t>& a, const std::vector<uint8_t>& b) {
    std::vector<uint8_t> result;
    result.reserve(a.size() + b.size());
    result.insert(result.end(), a.begin(), a.end());
    result.insert(result.end(), b.begin(), b.end());
    return result;
}

std::pair<std::vector<uint8_t>, std::vector<uint8_t>> ByteUtil::split(const std::vector<uint8_t>& merged, size_t aLength) {
    if (aLength > merged.size()) {
        if (aLength > merged.size()) {
            std::ostringstream oss;
            oss << "ByteUtil::split error: aLength (" << aLength
                << ") is larger than merged.size() (" << merged.size() << ")";
            throw std::out_of_range(oss.str());
        }
    }

    std::vector<uint8_t> a(merged.begin(), merged.begin() + aLength);
    std::vector<uint8_t> b(merged.begin() + aLength, merged.end());
    return {a, b};
}

std::string ByteUtil::bytesToHex(const std::vector<uint8_t>& v, bool upper) {
    return ByteUtil::bytesToHex(v.data(), v.size(), upper);
}

std::string ByteUtil::bytesToHex(const uint8_t* data, size_t len, bool upper) {
    const char* hex = upper ? HEX_UP : HEX_LO;

    std::string out;
    out.reserve(len * 2);
    for (size_t i = 0; i < len; ++i) {
        uint8_t v = data[i];
        out.push_back(hex[v >> 4]);
        out.push_back(hex[v & 0x0F]);
    }
    return out;
}

std::vector<uint8_t> ByteUtil::longToBytes(std::int64_t value) {
    std::vector<uint8_t> bytes(8);

    bytes[0] = (uint8_t)((value >> 56) & 0xFF);
    bytes[1] = (uint8_t)((value >> 48) & 0xFF);
    bytes[2] = (uint8_t)((value >> 40) & 0xFF);
    bytes[3] = (uint8_t)((value >> 32) & 0xFF);
    bytes[4] = (uint8_t)((value >> 24) & 0xFF);
    bytes[5] = (uint8_t)((value >> 16) & 0xFF);
    bytes[6] = (uint8_t)((value >> 8) & 0xFF);
    bytes[7] = (uint8_t)(value & 0xFF);

    return bytes;
}

std::int64_t ByteUtil::bytesToLong(const std::vector<uint8_t>& bytes) {
    if (bytes.size() != 8) {
        return 0;
    }

    std::int64_t value = 0;

    value |= (std::int64_t)bytes[0] << 56;
    value |= (std::int64_t)bytes[1] << 48;
    value |= (std::int64_t)bytes[2] << 40;
    value |= (std::int64_t)bytes[3] << 32;
    value |= (std::int64_t)bytes[4] << 24;
    value |= (std::int64_t)bytes[5] << 16;
    value |= (std::int64_t)bytes[6] << 8;
    value |= (std::int64_t)bytes[7];

    return value;
}

} // namespace team::cool::client::util

