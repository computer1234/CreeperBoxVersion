#pragma once

#include <string>

namespace team::cool::client::util::crypt {

class Combiner {
public:
    static std::string doKeys(const std::string& key1, const std::string& key2);
};

} // namespace team::cool::client::util::crypt

