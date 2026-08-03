//
// Created by mc_zh on 25-7-21.
//

#ifndef BYTE_H
#define BYTE_H
#include "../MacroFix.h"
#include <iostream>
#include <openssl/rand.h>

namespace encrypt {
    class Byte {
    private:
        unsigned char *data0;
        unsigned char *data1;
        unsigned short* hash;
        union {
            unsigned char KEY[4];
            uint32_t randomIV;
        };

        static __forceinline unsigned short calc_hash(unsigned char d0, unsigned char d1) {
            return (d0 << 8) ^ d1 ^ 0xA5C3;
        }

        [[nodiscard]] unsigned char encrypt_block(unsigned char value, int block_num,
                                           unsigned char prev_block) const {
            unsigned char key_part = KEY[block_num];
            return value ^ key_part ^ prev_block;
        }

        [[nodiscard]] unsigned char decrypt_block(unsigned char value, int block_num,
                                           unsigned char prev_block) const {
            unsigned char key_part = KEY[block_num];
            return value ^ key_part ^ prev_block;
        }

    public:
        __forceinline uint32_t getRandomIV() {
            return randomIV;
        }

        __forceinline uint16_t getEncryptedData() {
            return (static_cast<uint16_t>(*data1) << 8) |
                   static_cast<uint16_t>(*data0);
        }

        __forceinline uint16_t getHash() {
            return *hash;
        }

        explicit Byte(uint16_t encrypted, uint32_t iv, uint16_t hash) : randomIV(iv) {
            data0 = new unsigned char;
            data1 = new unsigned char;
            *data0 = static_cast<unsigned char>(encrypted & 0xFF);
            *data1 = static_cast<unsigned char>((encrypted >> 8) & 0xFF);
            this->hash = new unsigned short;
            *this->hash = hash;
        }

        explicit Byte(unsigned char value = 0) {
            RAND_bytes(reinterpret_cast<unsigned char*>(&randomIV), sizeof(randomIV));
            data0 = new unsigned char;
            data1 = new unsigned char;
            hash = new unsigned short;

            unsigned char parts[2];
            parts[0] = value & 0x0F;
            parts[1] = (value >> 4) & 0x0F;

            *data0 = encrypt_block(parts[0], 0, 0);
            *data1 = encrypt_block(parts[1], 1, *data0);
            *hash = calc_hash(*data0, *data1);
        }

        Byte(const Byte& other) {
            data0 = new unsigned char(*other.data0);
            data1 = new unsigned char(*other.data1);
            hash = new unsigned short(*other.hash);
            memcpy(KEY, other.KEY, 4);
        }

        ~Byte() {
            delete data0;
            delete data1;
            delete hash;
        }

        [[nodiscard]] unsigned char get() const;

        Byte operator+(const Byte& other) const {
            return Byte(this->get() + other.get());
        }

        Byte operator-(const Byte& other) const {
            return Byte(this->get() - other.get());
        }

        Byte& operator++() {
            unsigned char new_val = this->get() + 1;
            unsigned char parts[2];
            parts[0] = new_val & 0x0F;
            parts[1] = (new_val >> 4) & 0x0F;
            *data0 = encrypt_block(parts[0], 0, 0);
            *data1 = encrypt_block(parts[1], 1, *data0);
            *hash = calc_hash(*data0, *data1);
            return *this;
        }

        Byte& operator--() {
            unsigned char new_val = this->get() - 1;
            unsigned char parts[2];
            parts[0] = new_val & 0x0F;
            parts[1] = (new_val >> 4) & 0x0F;
            *data0 = encrypt_block(parts[0], 0, 0);
            *data1 = encrypt_block(parts[1], 1, *data0);
            *hash = calc_hash(*data0, *data1);
            return *this;
        }

        Byte operator++(int) {
            Byte temp = *this;
            ++(*this);
            return temp;
        }

        Byte operator--(int) {
            Byte temp = *this;
            --(*this);
            return temp;
        }

        bool operator==(const Byte& other) const {
            return this->get() == other.get();
        }

        bool operator!=(const Byte& other) const {
            return !(*this == other);
        }
    };
}

#endif