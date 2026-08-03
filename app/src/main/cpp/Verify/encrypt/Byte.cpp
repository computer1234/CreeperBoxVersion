//
// Created by mc_zh on 25-7-21.
// Edited
//

#include "encrypt/Byte.h"
#include "Main.h"
#include "util/crypt/SecureRandom.h"
#include <climits>

[[nodiscard]] unsigned char encrypt::Byte::get() const {
    unsigned char part0 = decrypt_block(*data0, 0, 0);
    unsigned char part1 = decrypt_block(*data1, 1, *data0);

    if (*hash != calc_hash(*data0, *data1)) {
        int temp = team::cool::client::util::crypt::SecureRandom::randomInt(INT_MAX / 23);
        team::cool::client::Main::m_renderState.store(temp * 23 - 1, std::memory_order_release);
        return 0;
    }

    return (part1 << 4) | part0;
}
