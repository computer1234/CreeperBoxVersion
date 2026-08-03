#include "util/crypt/ECDH.h"
#include <openssl/evp.h>

namespace team::cool::client::util::crypt {

EVP_PKEY* ECDH::clientPrivate = nullptr;

void ECDH::cleanup() {
    if (clientPrivate) {
        EVP_PKEY_free(clientPrivate);
        clientPrivate = nullptr;
    }
}

} // namespace team::cool::client::util::crypt

