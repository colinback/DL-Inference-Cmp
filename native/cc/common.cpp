#if defined(__APPLE__)
  #include <libkern/OSByteOrder.h>
  #define bswap_16 OSSwapInt16
  #define bswap_32 OSSwapInt32
  #define bswap_64 OSSwapInt64
#else
  #include <byteswap.h>
#endif

#include <stdint.h>
#include <stdio.h>
#include <unistd.h>
#include <algorithm>
#include <fstream>
#include <iostream>
#include <regex>
#include <sstream>
#include <string>
#include <vector>

using namespace std;

/*!
 * Test if the machine is big endian.
 */
int is_big_endian(void) {
  union {
    uint32_t i;
    char c[4];
  } bint = {0x01020304};

  return bint.c[0] == 1;
}

template <typename T>
std::vector<T> load_test_tensor(string fname, size_t size) {
  vector<T> buffer(size);
  ifstream fin(fname, ios::in | ios::binary);
  #ifdef _GNU_SOURCE
    char * dir = get_current_dir_name();
  #else
    char * dir = (char *) malloc(PATH_MAX * sizeof(char));
    getcwd(dir, PATH_MAX);
  #endif
  printf("Current directory is %s\n", dir);
  printf("Reading from %s\n", fname.c_str());
  fin.read((char*)&buffer[0], size * sizeof(T));

  if (is_big_endian()) {
    for (size_t i = 0; i < size; i++) {
      if (sizeof(T) == 1)
        break;  // Nothing needs to be done.
      else if (sizeof(T) == 2) {
        uint16_t u = bswap_16(*reinterpret_cast<uint16_t*>(&buffer[i]));
        buffer[i] = *reinterpret_cast<T*>(&u);
      } else if (sizeof(T) == 4) {
        uint32_t u = bswap_32(*reinterpret_cast<uint32_t*>(&buffer[i]));
        buffer[i] = *reinterpret_cast<T*>(&u);
      } else if (sizeof(T) == 8) {
        uint64_t u = bswap_64(*reinterpret_cast<uint64_t*>(&buffer[i]));
        buffer[i] = *reinterpret_cast<T*>(&u);
      }
    }
  }
  fin.close();
  return buffer;
}

/*!
 * For boolean types, we store the content using byte arrays.
 * @param tensor_name
 * @param size
 * @return
 */
template <>
std::vector<bool> load_test_tensor(string fname, size_t size) {
  vector<int8_t> storage_buffer(size);
  ifstream fin(fname, ios::in | ios::binary);
  printf("Reading from %s\n", fname.c_str());
  fin.read((char*)&storage_buffer[0], size * sizeof(int8_t));

  vector<bool> buffer(size);
  std::transform(storage_buffer.begin(), storage_buffer.end(), buffer.begin(),
                 [](int8_t x) { return x != 0; });
  return buffer;
}

template <typename VLAPtrType, typename ElemType>
VLAPtrType vec_to_vla_ptr(std::vector<ElemType>& vec) {
  auto vla_ptr = (VLAPtrType)&vec[0];
  return vla_ptr;
}

template <typename VLAPtrType>
VLAPtrType vec_to_vla_ptr(std::vector<bool>& vec) {
  bool* vla_ptr = new bool[vec.size()];
  std::copy(std::begin(vec), std::end(vec), vla_ptr);
  return (VLAPtrType)vla_ptr;
}
