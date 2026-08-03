#pragma once

#include <vector>
#include <cstdint>
#include <string>

namespace team::cool::client::util::crypt {

class HKDF {
public:
    static const std::vector<uint8_t>& getC2s();
    static const std::vector<uint8_t>& getS2c();
    
    static std::vector<uint8_t> generate(const std::vector<uint8_t>& ikm, 
                                        const std::vector<uint8_t>& salt, 
                                        const std::vector<uint8_t>& info, 
                                        size_t length);
};

} // namespace team::cool::client::util::crypt

