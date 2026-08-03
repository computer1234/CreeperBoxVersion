#ifndef INTEGER_H
#define LONG64_H
#include "../MacroFix.h"

#include <iostream>
#include <openssl/rand.h>


namespace encrypt {

    class Long64 {

    private:
        unsigned short *data0;
        unsigned short *data1;
        unsigned short *data2;
        unsigned short *data3;
        unsigned int* hash;

        union {
            unsigned char KEY[8];
            uint64_t randomIV;
        };


        static __forceinline unsigned int calc_hash(unsigned short d0, unsigned short d1,
                                        unsigned short d2, unsigned short d3) {
            return (d0 << 16) ^ d1 ^ (d2 << 16) ^ d3;
        }

        [[nodiscard]] unsigned short encrypt_block(unsigned short value, int block_num,
                                           unsigned short prev_block) const {
            unsigned short key_part = (KEY[block_num*2] << 8) | KEY[block_num*2+1];
            return value ^ key_part ^ prev_block;
        }

        [[nodiscard]] unsigned short decrypt_block(unsigned short value, int block_num,
                                           unsigned short prev_block) const {
            unsigned short key_part = (KEY[block_num*2] << 8) | KEY[block_num*2+1];
            return value ^ key_part ^ prev_block;
        }

    public:

        __forceinline uint64_t getRandomIV() {
            return randomIV;
        }

        __forceinline uint64_t getEncryptedData() {
            return (static_cast<uint64_t>(*data1) << 48) |
                   (static_cast<uint64_t>(*data3) << 32) |
                   (static_cast<uint64_t>(*data0) << 16) |
                   static_cast<uint64_t>(*data2);
        }

        __forceinline uint32_t getHash() {
            return *hash;
        }


        explicit Long64(uint64_t encrypted,uint64_t iv,uint32_t hash) : randomIV(iv) {
            data0 = new unsigned short;
            data1 = new unsigned short;
            data2 = new unsigned short;
            data3 = new unsigned short;
            *data0 = static_cast<unsigned short>((encrypted >> 16) & 0xFFFF);
            *data1 = static_cast<unsigned short>((encrypted >> 48) & 0xFFFF);
            *data2 = static_cast<unsigned short>(encrypted & 0xFFFF);
            *data3 = static_cast<unsigned short>((encrypted >> 32) & 0xFFFF);
            this->hash = new unsigned int;
            *this->hash = hash;
        }

        explicit Long64(uint64_t value = 0) {
            RAND_bytes(reinterpret_cast<unsigned char*>(&randomIV), sizeof(randomIV));

            data0 = new unsigned short;
            data1 = new unsigned short;
            data2 = new unsigned short;
            data3 = new unsigned short;
            hash = new unsigned int;

            unsigned short parts[4];
            parts[0] = static_cast<unsigned short>(value >> 48);
            parts[1] = static_cast<unsigned short>(value >> 32);
            parts[2] = static_cast<unsigned short>(value >> 16);
            parts[3] = static_cast<unsigned short>(value);

            *data0 = encrypt_block(parts[0], 0, 0);
            *data1 = encrypt_block(parts[1], 1, *data0);
            *data2 = encrypt_block(parts[2], 2, *data1);
            *data3 = encrypt_block(parts[3], 3, *data2);

            *hash = calc_hash(*data0, *data1, *data2, *data3);
        }



        Long64(const Long64& other) {
            data0 = new unsigned short(*other.data0);
            data1 = new unsigned short(*other.data1);
            data2 = new unsigned short(*other.data2);
            data3 = new unsigned short(*other.data3);
            hash = new unsigned int(*other.hash);
            memcpy(KEY, other.KEY, 8);
        }

        ~Long64() {
            delete data0;
            delete data1;
            delete data2;
            delete data3;
            delete hash;
        }

        [[nodiscard]] int64_t get() const;

        Long64 operator+(const Long64& other) const {
            return Long64(this->get() + other.get());
        }

        Long64 operator-(const Long64& other) const {
            return Long64(this->get() - other.get());
        }

        Long64& operator++() {
            int64_t new_val = this->get() + 1;
            unsigned short parts[4];
            parts[0] = static_cast<unsigned short>(new_val >> 48);
            parts[1] = static_cast<unsigned short>(new_val >> 32);
            parts[2] = static_cast<unsigned short>(new_val >> 16);
            parts[3] = static_cast<unsigned short>(new_val);

            *data0 = encrypt_block(parts[0], 0, 0);
            *data1 = encrypt_block(parts[1], 1, *data0);
            *data2 = encrypt_block(parts[2], 2, *data1);
            *data3 = encrypt_block(parts[3], 3, *data2);

            *hash = calc_hash(*data0, *data1, *data2, *data3);
            return *this;
        }

        Long64& operator--() {
            int64_t new_val = this->get() - 1;
            unsigned short parts[4];
            parts[0] = static_cast<unsigned short>(new_val >> 48);
            parts[1] = static_cast<unsigned short>(new_val >> 32);
            parts[2] = static_cast<unsigned short>(new_val >> 16);
            parts[3] = static_cast<unsigned short>(new_val);

            *data0 = encrypt_block(parts[0], 0, 0);
            *data1 = encrypt_block(parts[1], 1, *data0);
            *data2 = encrypt_block(parts[2], 2, *data1);
            *data3 = encrypt_block(parts[3], 3, *data2);

            *hash = calc_hash(*data0, *data1, *data2, *data3);
            return *this;
        }

        Long64 operator++(int) {
            Long64 temp = *this;
            ++(*this);
            return temp;
        }

        Long64 operator--(int) {
            Long64 temp = *this;
            --(*this);
            return temp;
        }

        bool operator==(const Long64& other) const {
            return this->get() == other.get();
        }

        bool operator!=(const Long64& other) const {
            return !(*this == other);
        }

    };
}


#endif
