#include "Config.h"
#include "util/Generator.h"

namespace team::cool::client {

    std::string Config::username = "test";
    std::string Config::password = "test";
    std::string Config::productType = "test";
    std::string Config::productVersion = "1.0.0";
    std::string Config::other;
    std::string Config::cookie = util::Generator::randomString(64);  // 随机生成64位cookie
    std::string Config::hardwareId = "test";
    std::map<std::string, std::string> Config::hardwareMap;

}