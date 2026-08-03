#include "encrypt/Long64.h"
#include "Main.h"
#include "util/crypt/SecureRandom.h"
#include <climits>

[[nodiscard]] int64_t encrypt::Long64::get() const {

    unsigned short part0 = decrypt_block(*data0, 0, 0);
    unsigned short part1 = decrypt_block(*data1, 1, *data0);
    unsigned short part2 = decrypt_block(*data2, 2, *data1);
    unsigned short part3 = decrypt_block(*data3, 3, *data2);

    if (*hash != calc_hash(*data0, *data1, *data2, *data3)) {
        int temp = team::cool::client::util::crypt::SecureRandom::randomInt(INT_MAX / 23);
        team::cool::client::Main::m_renderState.store(temp * 23 - 1, std::memory_order_release);
        return 0;
    }

    return (static_cast<int64_t>(part0) << 48) |
           (static_cast<int64_t>(part1) << 32) |
           (static_cast<int64_t>(part2) << 16) |
           part3;
}
