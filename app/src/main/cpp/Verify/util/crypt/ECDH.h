#pragma once

#include <openssl/evp.h>
#include <memory>

namespace team::cool::client::util::crypt {

class ECDH {
public:
    static EVP_PKEY* clientPrivate;
    
    static void cleanup();
};

} // namespace team::cool::client::util::crypt

