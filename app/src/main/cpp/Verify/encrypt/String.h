#ifndef STRING_H
#define STRING_H
#include "../MacroFix.h"
#include <algorithm>
#include <iostream>
#include "util/ByteUtil.h"

namespace encrypt {

    template<char _key1,char _key2>
    class String {
    private:
        uint64_t _size;
        char* _storage;
        bool _isEncrypted = false;

        _forceinline constexpr void crypt(const char* data)
        {
            for (int i = 0; i < _size; i++)
            {
                _storage[i] = data[i] ^ (_key1 + i % (1 + _key2));
            }
        }

    public:
        template <size_t N>
            __forceinline constexpr String(const char(&str)[N])
                : _size(N - 1),
                  _storage(new char[N - 1])
        {
            std::copy(str, str + _size, _storage);
            crypt(const_cast<char*>(str));
        }

        __forceinline String& operator=(const String& other) {
            if (this != &other) {
                clear();
                _size = other._size;
                _storage = new char[_size + 1];
                std::copy(other._storage, other._storage + _size, _storage);
                _storage[_size] = '\0';
            }
            return *this;
        }

         __forceinline constexpr String(const char* str,uint64_t size)
        : _size(size),
          _storage(new char[size+1])
        {
            std::copy(str, str + _size, _storage);
            _storage[_size] = '\0';
        }

        __forceinline String(const String& other)
        : _size(other._size),
          _storage(new char[other._size])
        {
            std::copy(other._storage, other._storage + _size, _storage);
        }

        __forceinline String(String&& other) noexcept
       : _size(other._size),
         _storage(other._storage),
         _isEncrypted(other._isEncrypted)
        {
            other._size = 0;
            other._storage = nullptr;
        }

        __forceinline ~String() {
            clear();
        }

        __forceinline void setEncrypted(bool value)
        {
            _isEncrypted = value;
        }

        __forceinline bool isEncrypted()
        {
            return _isEncrypted;
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


        __forceinline int size()
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
        }

        __forceinline const char* c_str() {
            decrypt();
            return _storage;
        }

    };

}




#endif //STRING_H
