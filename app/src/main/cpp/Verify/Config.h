#pragma once

#include <string>
#include <map>

namespace team::cool::client {

class Config {

private:
    static std::string username; // 登录用户名
    static std::string password; // 登录密码
    static std::string productType; // 登录产品类型
    static std::string productVersion; // 登录产品版本号
    static std::string other; // 附加信息，可自定义，自己在服务端写处理
    static std::string cookie; // 64位随机cookie，用于获取时间API
    static std::string hardwareId; // 用户HWID(唯一辨识码)
    static std::map<std::string, std::string> hardwareMap; // 用户电脑信息
public:
    static std::string getUsername() { return username; }
    static std::string getPassword() { return password; }
    static std::string getProductType() { return productType; }
    static std::string getProductVersion() { return productVersion; }
    static std::string getOther() { return other; }
    static std::string getCookie() { return cookie; }
    static std::string getHardwareId() { return hardwareId; }
    static std::map<std::string, std::string> getHardwareMap() { return hardwareMap; }
    
    // Setters
    static void setUsername(const std::string& u) { username = u; }
    static void setPassword(const std::string& p) { password = p; }
    static void setProductType(const std::string& t) { productType = t; }
    static void setProductVersion(const std::string& v) { productVersion = v; }
    static void setOther(const std::string& o) { other = o; }
    static void setHardwareId(const std::string& h) { hardwareId = h; }
    static void setHardwareMap(const std::map<std::string, std::string>& m) { hardwareMap = m; }
};

}
