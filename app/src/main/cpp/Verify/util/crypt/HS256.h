#pragma once

#include <string>
#include <vector>
#include <cstdint>

namespace team::cool::client::util::crypt {

class HS256 {
private:
    static std::string key;
    static constexpr const char* SECRET = "first_fuck_the_world_shit_world_fuck_your_mother_i_hate_this_world";

public:
    static void setKey(const std::string& k) { key = k; }
    static const std::string& getKey() { return key; }
    
    static std::string signS2C(const std::vector<uint8_t>& data);
    static std::string signC2S(const std::vector<uint8_t>& data);
    static bool verify(const std::vector<uint8_t>& data, const std::string& signature);
};

} // namespace team::cool::client::util::crypt

