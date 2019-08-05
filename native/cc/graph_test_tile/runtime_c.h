#pragma once

#if defined(__APPLE__)
  #include <libkern/OSByteOrder.h>
  #define bswap_16 OSSwapInt16
  #define bswap_32 OSSwapInt32
  #define bswap_64 OSSwapInt64
#else
  #include <byteswap.h>
#endif

#define __USE_GNU
#include <dlfcn.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef DLC_CUDA
#include <algorithm>
#include <cassert>
#include <chrono>
#include <iostream>
#include <ostream>
#include <string>
#include <vector>

#include <cublas.h>
#include <cublas_v2.h>
#include <cuda.h>
#include <cudnn.h>

void print_mem_usage() {
  // show memory usage of GPU

  size_t free_byte;
  size_t total_byte;

  auto cuda_status = cudaMemGetInfo(&free_byte, &total_byte);
  if (cudaSuccess != cuda_status) {
    printf("Error: cudaMemGetInfo fails, %s \n",
           cudaGetErrorString(cuda_status));
    exit(1);
  }

  double free_db = (double)free_byte;
  double total_db = (double)total_byte;
  double used_db = total_db - free_db;

  printf("GPU memory usage: used = %f, free = %f MB, total = %f MB\n",
         used_db / 1024.0 / 1024.0, free_db / 1024.0 / 1024.0,
         total_db / 1024.0 / 1024.0);
}

#include "dlc_cudnn.cu"

#endif

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

// Find the path where our .so is loaded and this is where weights
// directory is. If dladdr fails, weights directory is assume to be
// in the current working directory, which may NOT be where the .so is.
char *ResolveLoadPath(char *fname) {
  static char *loadpath = NULL;
  Dl_info dlinfo;

  // Resolve .so load path once using function "is_big_endian"
  if (loadpath == NULL) {
    if (dladdr(is_big_endian, &dlinfo) == 0)  return fname;

    int len = strrchr(dlinfo.dli_fname, '/') - dlinfo.dli_fname + 1;
    if ((loadpath = strndup(dlinfo.dli_fname, len)) == NULL) return fname;
  }

  char *pname;
  if ((pname = malloc(strlen(loadpath)+strlen(fname)+1)) == NULL) return fname;
  strcpy(pname, loadpath);
  strcat(pname, fname);
  return pname;
}

/*!
 * Read tensors from file.
 * @param name name of the tensor.
 * @return pointer to tensor content in memory.
 */
void *ReadTensor(void *env, char *name, char *type, void **buf) {
  int bswapSize =
    !strcmp(type, "float")   ? sizeof(float) :
    !strcmp(type, "int32_t") ? sizeof(int32_t) :
    !strcmp(type, "int64_t") ? sizeof(int64_t) :
    !strcmp(type, "bool")    ? sizeof(char) : -1;
  if (bswapSize < 0) {
    fprintf(stderr, "Tensor type %s unknown\n", type);
    exit(-1);
  }

  FILE *file;
  unsigned long fileLen;
  // Open file
  file = fopen(ResolveLoadPath(name), "rb");
  if (!file) {
    fprintf(stderr, "Unable to open file %s\n", name);
    exit(1);
  }
  // Get file length
  fseek(file, 0, SEEK_END);
  fileLen = ftell(file);
  if (fileLen % bswapSize) {
    fprintf(stderr, "File length %ld error!\n", fileLen);
    fclose(file);
    exit(2);
  }
  fseek(file, 0, SEEK_SET);
  // Allocate memory if not already allocated
  if (*buf == NULL && (*buf = malloc(fileLen)) == NULL) {
    fprintf(stderr, "Memory error!\n");
    fclose(file);
    exit(3);
  }
  // Read file contents into buffer, byte swapping on big endian system
  char *buffer = *buf;
  fread(buffer, fileLen, 1, file);
  if (is_big_endian()) {
    for (unsigned long i = 0; i < fileLen / bswapSize; i++) {
      if (bswapSize == 1)
        break; // Nothing needs to be done.
      else if (bswapSize == 2)
        ((uint16_t *)buffer)[i] = bswap_16(((uint16_t *)buffer)[i]);
      else if (bswapSize == 4)
        ((uint32_t *)buffer)[i] = bswap_32(((uint32_t *)buffer)[i]);
      else if (bswapSize == 8)
        ((uint64_t *)buffer)[i] = bswap_64(((uint64_t *)buffer)[i]);
      else {
        fprintf(stderr, "Unrecognized byte swap size!\n");
        fclose(file);
        exit(4);
      }
    }
  }
  fclose(file);
  return buffer;
}

void ReleaseTensor(void *env, char *type, void *buf, void *alias) {
  free(buf);
}

void DumpTensor(char *name, void *tensor_ptr, size_t len) {
  FILE *f = fopen(name, "wb");
  if (!f)
    perror("fopen");
  fwrite(tensor_ptr, sizeof(char), len, f);
  fclose(f);
}

void PrintFloatTensor(float *tensor_ptr, size_t len) {
  for (int i = 0; i < len; i++)
    printf("output[%d] = %f\n", i, tensor_ptr[i]);
}
