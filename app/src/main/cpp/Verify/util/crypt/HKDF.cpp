#include "util/crypt/HKDF.h"
#include <openssl/evp.h>
#include <openssl/kdf.h>
#include <stdexcept>
#include <cstring>

namespace team::cool::client::util::crypt {

static const std::vector<uint8_t> c2s_info = []() {
    const char* text = 
        "这个世界太凄凉，人心全都冰凉凉，\n"
        "街头巷尾风吹过，落叶飘零无人赏。\n"
        "高楼林立影幢幢，灯火虽明却空荡，\n"
        "行人匆匆擦肩去，眼神冷漠如霜降。\n"
        "友情如烟散得快，爱情如冰碎成浆，\n"
        "承诺昨日还温暖，转眼化作寒风狂。\n"
        "山川虽美无人恋，江河虽阔浪滔滔，\n"
        "却见孤舟漂零去，载满忧愁无处藏。\n"
        "夜深人静思往事，泪水悄然湿衣裳，\n"
        "这个世界太凄凉，人心全都冰凉凉。\n"
        "何时能见一丝暖，融化这无尽的霜？\n"
        "但愿春风来拂面，重唤人间旧时光。\n";
    return std::vector<uint8_t>(text, text + strlen(text));
}();

static const std::vector<uint8_t> s2c_info = []() {
    const char* text = 
        "尘世喧嚣如梦幻，灵魂漂泊无依傍，\n"
        "霓虹闪烁夜未央，繁华背后藏凄凉。\n"
        "车水马龙路迢迢，笑脸虚假心彷徨，\n"
        "旧友相逢不相识，回忆如刀刺心房。\n"
        "梦想破碎成碎片，希望如雾散无踪，\n"
        "金钱堆积成高墙，阻挡真情暖阳光。\n"
        "花开虽艳无人赏，鸟鸣虽甜耳不闻，\n"
        "独坐窗前望星空，寂寞如潮涌四方。\n"
        "晨曦初现思前路，叹息声声入耳旁，\n"
        "尘世喧嚣如梦幻，灵魂漂泊无依傍。\n"
        "何时能觅一缕光，驱散这漫漫黑暗？\n"
        "但愿秋叶落尽时，重现人间真情长。\n";
    return std::vector<uint8_t>(text, text + strlen(text));
}();

const std::vector<uint8_t>& HKDF::getC2s() {
    return c2s_info;
}

const std::vector<uint8_t>& HKDF::getS2c() {
    return s2c_info;
}

std::vector<uint8_t> HKDF::generate(const std::vector<uint8_t>& ikm, 
                                     const std::vector<uint8_t>& salt, 
                                     const std::vector<uint8_t>& info, 
                                     size_t length) {
    EVP_PKEY_CTX* pctx = EVP_PKEY_CTX_new_id(EVP_PKEY_HKDF, nullptr);
    if (!pctx) throw std::runtime_error("Failed to create HKDF context");
    
    std::vector<uint8_t> okm(length);
    std::vector<uint8_t> saltVec = salt.empty() ? std::vector<uint8_t>(32, 0) : salt;
    
    if (EVP_PKEY_derive_init(pctx) <= 0 ||
        EVP_PKEY_CTX_set_hkdf_md(pctx, EVP_sha256()) <= 0 ||
        EVP_PKEY_CTX_set1_hkdf_salt(pctx, saltVec.data(), saltVec.size()) <= 0 ||
        EVP_PKEY_CTX_set1_hkdf_key(pctx, ikm.data(), ikm.size()) <= 0 ||
        EVP_PKEY_CTX_add1_hkdf_info(pctx, info.data(), info.size()) <= 0 ||
        EVP_PKEY_derive(pctx, okm.data(), &length) <= 0) {
        EVP_PKEY_CTX_free(pctx);
        throw std::runtime_error("HKDF derivation failed");
    }
    
    EVP_PKEY_CTX_free(pctx);
    return okm;
}

} // namespace team::cool::client::util::crypt

