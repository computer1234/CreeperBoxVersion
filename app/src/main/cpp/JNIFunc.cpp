#include <jni.h>
#include "SDK/Actor.h"
#include "Utils/Logger.h"
#include "SDK/BlockSource.h"
#include "Memory/GameData.h"
#include "Utils/JNIUtils.h"
#include "SDK/Render2D.h"
#include "SDK/Render3D.h"
#include "Include/Arm64InlineHook/arm64_inlinehook.h"
#include <vector>
#include "JavaData.h"
#include "Memory/Hooks.h"
#include "JNIHelpers.h"
#include "SDK/MobileClient.h"
#include <functional>
#include <fstream>
#include <sstream>
#include <chrono>
#include <sys/system_properties.h>

// Helper Functions
std::string toHexLog(const std::string* str) {
    std::string hexString;
    if (str == nullptr) {
        return std::string();
    }
    for (unsigned char c : *str) {
        char buffer[3];
        snprintf(buffer, sizeof(buffer), "%02x", c);
        hexString += buffer;
    }
    return hexString;
}

void get_device_info(char *output_hash) {
    char model[128] = {0};
    char board[128] = {0};
    char device[128] = {0};
    
    __system_property_get("ro.product.model", model);
    __system_property_get("ro.product.board", board);
    __system_property_get("ro.product.device", device);
    
    // 如果所有值都为空，使用默认值
    if (strlen(model) == 0) strcpy(model, "unknown");
    if (strlen(board) == 0) strcpy(board, "unknown");
    if (strlen(device) == 0) strcpy(device, "unknown");
    
    std::string _model = std::string(model).substr(0, 8);
    std::string _board = std::string(board).substr(0, 8);
    std::string _device = std::string(device).substr(0, 8);
    
    std::string baseCode = _model + _board + _device + _model + _board + _device;
    
    // 确保 baseCode 至少有 16 个字符
    while (baseCode.length() < 16) {
        baseCode += "X";
    }
    
    snprintf(output_hash, 17, "%s%s%s%s",
             baseCode.substr(0, 4).c_str(),
             baseCode.substr(3, 4).c_str(),
             baseCode.substr(7, 4).c_str(),
             baseCode.substr(11, 4).c_str());
    
    LOGI("HWID 生成: %s (model=%s, board=%s, device=%s)", output_hash, model, board, device);
}




// CreeperBox Methods




