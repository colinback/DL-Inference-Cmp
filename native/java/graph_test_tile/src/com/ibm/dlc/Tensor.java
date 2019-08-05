package com.ibm.dlc;

public class Tensor {

  public static void concat_set(Cfloat t, int i0, int i1, int i2, float val) {
    int idx =
      0 +
      i0 * 6 * 2 +
      i1 * 2 +
      i2;
    t.set(idx, val);
  }

  public static float concat_get(Cfloat t, int i0, int i1, int i2) {
    int idx =
      0 +
      i0 * 6 * 2 +
      i1 * 2 +
      i2;
    return t.get(idx);
  }

  public static void concat_1_set(Cfloat t, int i0, int i1, int i2, float val) {
    int idx =
      0 +
      i0 * 24 * 2 +
      i1 * 2 +
      i2;
    t.set(idx, val);
  }

  public static float concat_1_get(Cfloat t, int i0, int i1, int i2) {
    int idx =
      0 +
      i0 * 24 * 2 +
      i1 * 2 +
      i2;
    return t.get(idx);
  }

  public static void concat_2_set(Cfloat t, int i0, int i1, int i2, float val) {
    int idx =
      0 +
      i0 * 24 * 6 +
      i1 * 6 +
      i2;
    t.set(idx, val);
  }

  public static float concat_2_get(Cfloat t, int i0, int i1, int i2) {
    int idx =
      0 +
      i0 * 24 * 6 +
      i1 * 6 +
      i2;
    return t.get(idx);
  }

  public static void repeats_set(Cint32_t t, int i0, int val) {
    int idx =
      0 +
      i0;
    t.set(idx, val);
  }

  public static int repeats_get(Cint32_t t, int i0) {
    int idx =
      0 +
      i0;
    return t.get(idx);
  }

  public static void t1_set(Cfloat t, int i0, int i1, int i2, float val) {
    int idx =
      0 +
      i0 * 6 * 2 +
      i1 * 2 +
      i2;
    t.set(idx, val);
  }

  public static float t1_get(Cfloat t, int i0, int i1, int i2) {
    int idx =
      0 +
      i0 * 6 * 2 +
      i1 * 2 +
      i2;
    return t.get(idx);
  }

}
