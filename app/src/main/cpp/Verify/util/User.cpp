#include "util/User.h"

namespace team::cool::client::util {

int64_t User::userId = 0;
std::string User::username;
bool User::beta = false;
std::chrono::system_clock::time_point User::expiryTime;

} // namespace team::cool::client::util

