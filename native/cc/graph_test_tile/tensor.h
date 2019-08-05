#pragma once 
#include "string.h"
#define concat_set(concat, i0, i1, i2,  val) \
  concat[ \
    0 + \
    i0 * 6 * 2 + \
    i1 * 2 + \
    i2] = (val); 
#define concat_get(concat, i0, i1, i2) \
  concat[ \
    0 + \
    i0 * 6 * 2 + \
    i1 * 2 + \
    i2] 
#define concat_1_set(concat_1, i0, i1, i2,  val) \
  concat_1[ \
    0 + \
    i0 * 24 * 2 + \
    i1 * 2 + \
    i2] = (val); 
#define concat_1_get(concat_1, i0, i1, i2) \
  concat_1[ \
    0 + \
    i0 * 24 * 2 + \
    i1 * 2 + \
    i2] 
#define concat_2_set(concat_2, i0, i1, i2,  val) \
  concat_2[ \
    0 + \
    i0 * 24 * 6 + \
    i1 * 6 + \
    i2] = (val); 
#define concat_2_get(concat_2, i0, i1, i2) \
  concat_2[ \
    0 + \
    i0 * 24 * 6 + \
    i1 * 6 + \
    i2] 
#define repeats_set(repeats, i0,  val) \
  repeats[ \
    0 + \
    i0] = (val); 
#define repeats_get(repeats, i0) \
  repeats[ \
    0 + \
    i0] 
#define t1_set(t1, i0, i1, i2,  val) \
  t1[ \
    0 + \
    i0 * 6 * 2 + \
    i1 * 2 + \
    i2] = (val); 
#define t1_get(t1, i0, i1, i2) \
  t1[ \
    0 + \
    i0 * 6 * 2 + \
    i1 * 2 + \
    i2] 
