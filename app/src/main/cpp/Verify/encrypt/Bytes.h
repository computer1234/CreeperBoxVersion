#ifndef BYTES_H
#define BYTES_H
#include "../MacroFix.h"
#include <algorithm>
#include <iostream>
#include <stdexcept>

namespace encrypt {
    template<char _key1, char _key2>
    class Bytes {
    private:
        uint64_t _size;
        char* _storage;
        bool _isEncrypted;

        __forceinline constexpr void crypt(char* data)
        {
            for (uint64_t i = 0; i < _size; i++)
            {
                data[i] = data[i] ^ (_key1 + i % (1 + _key2));
            }
        }

    public:
        template <size_t N>
        __forceinline constexpr Bytes(const unsigned char(&data)[N])
            : _size(N),
              _storage(new char[N]),
              _isEncrypted(false)
        {
            std::copy(reinterpret_cast<const char*>(data),
                     reinterpret_cast<const char*>(data) + _size, _storage);
        }

        template <size_t N>
        __forceinline constexpr Bytes(const char(&data)[N])
            : _size(N),
              _storage(new char[N]),
              _isEncrypted(false)
        {
            std::copy(data, data + _size, _storage);
        }

        __forceinline Bytes& operator=(const Bytes& other) {
            if (this != &other) {
                clear();
                _size = other._size;
                _isEncrypted = other._isEncrypted;
                _storage = new char[_size];
                std::copy(other._storage, other._storage + _size, _storage);
            }
            return *this;
        }

        __forceinline constexpr Bytes(const unsigned char* data, uint64_t size)
            : _size(size),
              _storage(new char[size]),
              _isEncrypted(false)
        {
            std::copy(reinterpret_cast<const char*>(data),
                     reinterpret_cast<const char*>(data) + _size, _storage);
        }

        __forceinline constexpr Bytes(const char* data, uint64_t size)
            : _size(size),
              _storage(new char[size]),
              _isEncrypted(false)
        {
            std::copy(data, data + _size, _storage);
        }

        __forceinline Bytes(const Bytes& other)
            : _size(other._size),
              _storage(new char[other._size]),
              _isEncrypted(other._isEncrypted)
        {
            std::copy(other._storage, other._storage + _size, _storage);
        }

        __forceinline Bytes(Bytes&& other) noexcept
            : _size(other._size),
              _storage(other._storage),
              _isEncrypted(other._isEncrypted)
        {
            other._size = 0;
            other._storage = nullptr;
            other._isEncrypted = false;
        }

        __forceinline ~Bytes() {
            clear();
        }

        __forceinline bool isEncrypted() const
        {
            return _isEncrypted;
        }

        __forceinline void setEncrypted(bool value)
        {
            _isEncrypted = value;
        }

        __forceinline char* encrypt()
        {
            if (!_isEncrypted) {
                crypt(_storage);
                _isEncrypted = true;
            }
            return _storage;
        }

        __forceinline char* decrypt()
        {
            if (_isEncrypted) {
                crypt(_storage);
                _isEncrypted = false;
            }
            return _storage;
        }

        __forceinline uint64_t size() const
        {
            return _size;
        }

        __forceinline void clear()
        {
            if (_storage) {
                std::fill_n(_storage, _size, 0);
                delete[] _storage;
                _storage = nullptr;
            }
            _size = 0;
            _isEncrypted = false;
        }

        __forceinline const char* data() {
            decrypt();
            return _storage;
        }

        __forceinline const unsigned char* udata() {
            decrypt();
            return reinterpret_cast<const unsigned char*>(_storage);
        }

        __forceinline char* raw_data() {
            return _storage;
        }

        __forceinline unsigned char operator[](uint64_t index) const {
            if (index >= _size) {
                throw std::out_of_range("Bytes index out of range");
            }
            return static_cast<unsigned char>(_storage[index]);
        }
    };
}

#endif //BYTES_H