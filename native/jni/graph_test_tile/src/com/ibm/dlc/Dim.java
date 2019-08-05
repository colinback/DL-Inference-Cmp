package com.ibm.dlc;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Dim {
  /*
   * Return the size of each dimension and optionally the total size of a
   * multidimensional array. For example, an array of float[2][3][4]
   * will return [2, 3, 4, 24], where 24 = 2x3x4 is the total size
   * of the array.
   *
   * f = TRUE: return total size
   */
  public static int[] dim(Object a, boolean f) {
    ArrayList<Integer> d = new ArrayList<Integer>();
    int size = 1;
    /* Loop until subcomponent is not an array */
    for (Class<?> cls = a.getClass(); cls.isArray(); cls = cls.getComponentType()) {
      /* Number of elements on current dimension */
      int l = Array.getLength(a);
      /* Accumulate total size */
      d.add(l);
      size *= l;
      /* Advance to next dimension */
      a = Array.get(a, 0);
    }
    if (f) d.add(size);

    /* Note this requires Java 1.8 */
    return d.stream().mapToInt(i -> i).toArray();
  }

  /*
   * Recursively "flatten" an arbitrary dimension array src
   * into an one-dimensional array dst.
   */
  public static void flatten(Object src, int[] srcDim, int dim, Object dst, int[] idx) {
    /* Number of elements on this dimension */
    int length = srcDim[dim];
    /* This is the last dimension, copy elements from src to dst */
    if (dim == srcDim.length - 2) {
      System.arraycopy(src, 0, dst, idx[0], length);
      idx[0] += length;
    }
    /* Loop through sub-arrays, flatten each recursively */
    else {
      for (int i = 0; i < length; i++)
        flatten(Array.get(src, i), srcDim, dim+1, dst, idx);
    }
  }

  /*
   * Recursively "unflatten" an one-dimensional array src
   * into an arbitrary dimension array dst.
   */
  public static void unflatten(Object dst, int[] dstDim, int dim, Object src, int[] idx) {
    /* Number of elements on this dimension */
    int length = dstDim[dim];
    /* This is the last dimension, copy elements from src to dst */
    if (dim == dstDim.length - 2) {
      System.arraycopy(src, idx[0], dst, 0, length);
      idx[0] += length;
    }
    /* Loop through sub-arrays, unflatten each recursively */
    else {
      for (int i = 0; i < length; i++)
        unflatten(Array.get(dst, i), dstDim, dim+1, src, idx);
    }
  }

  public static void print(Object a) {
      print(a, "");
  }

  /*
   * Recursively print an arbitrary dimension array.
   */
  public static void print(Object a, String p) {
    Class<?> cls = a.getClass().getComponentType();
    if (cls.isArray()) {
      for (int i = 0; i < Array.getLength(a); i++)
        print(Array.get(a, i), p + "[" + i + "]");
    }
    else {
      String t = a.getClass().getComponentType().getSimpleName();
      for (int i = 0; i < Array.getLength(a); i++)
        System.out.println(t + " " + p + "[" + i + "]=" + Array.get(a, i));
    }
  }
}
