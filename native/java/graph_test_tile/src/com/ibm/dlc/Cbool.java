package com.ibm.dlc;

import java.util.Arrays;

/*
 * Multidimensional byte array that allows sharing of the underlying
 * data among multiple instances of arbitrary dimensions. This emulates
 * the effect of pointer type cast from one multidimensional array to
 * another in C/C++, such as from byte[3][8] to byte[2][3][4].
 */
public class Cbool {
  private int size;     // total size of the array, max 2^31-1
  private int offset;   // offset into data array where our index starts
  private int[] dims;   // dimensions of the array
  private byte[] data;  // linear data array backing the multidimensional array

  /*
   * Default constructor with newly allocated data array.
   * Dimensions are specified as an array of integers. For example,
   * [ 2, 3, 4 ] denotes an array of byte[2][3][4].
   * An empty dimension array is explicitly converted to int[]{1}.
   */
  public Cbool(int[] d) {
    dims = d.length == 0 ? new int[]{1} : d;
    size = size(dims);
    data = new byte[size];
    offset = 0;
  }

  /*
   * Constructor with supplied byte[] as the data array, starting
   * at the offset 0, having the same size as b.
   */
  public Cbool(byte[] b) {
    this(new int[]{b.length}, b, 0);
  }

  /*
   * Constructor with supplied byte[] as the data array, starting
   * at the offset 0. Note d does not need to map the entire b.
   */
  public Cbool(int[] d, byte[] b) {
    this(d, b, 0);
  }

  /*
   * Constructor with supplied byte[] as the data array, starting
   * at the offset n into b. Note d does not need to map the entire b.
   */
  public Cbool(int[] d, byte[] b, int n) {
    if (n < 0)
      throw new IllegalArgumentException("offset is negative");
    dims = d.length == 0 ? new int[]{1} : d;
    size = size(dims);
    if (n + size < 0)
      throw new IllegalArgumentException("offset + array size overflow");
    if (n + size > b.length)
      throw new IllegalArgumentException("offset + array size out of bound");
    data = b;
    offset = n;
  }

  /*
   * Constructor sharing underlying data array with another Cbool,
   * starting at the offset 0, having the same dimensions as b.
   */
  public Cbool(Cbool b) {
    this(b.dim(), b, 0);
  }

  /*
   * Constructor sharing underlying data array with another Cbool,
   * starting at the offset 0. Note d and b can be any arbitrary
   * dimensions and d does not need to map the entire b.
   */
  public Cbool(int[] d, Cbool b) {
    this(d, b, 0);
  }

  /*
   * Constructor sharing underlying data array with another Cbool,
   * starting at the offset n into b. Note d and b can be any arbitrary
   * dimensions and d does not need to map the entire b.
   */
  public Cbool(int[] d, Cbool b, int n) {
    if (n < 0)
      throw new IllegalArgumentException("offset is negative");
    dims = d.length == 0 ? new int[]{1} : d;
    size = size(dims);
    if (n + size < 0)
      throw new IllegalArgumentException("offset + array size overflow");
    if (n + size > b.size())
      throw new IllegalArgumentException("offset + array size out of bound");
    data = b.data;
    offset = n + b.offset;
  }

  /*
   * Compute the total size of a multidimensional array.
   */
  private int size(int[] d) {
    int size = 1;
    for (int i = 0; i < d.length; i++) {
      if (d[i] == 0)
        throw new IllegalArgumentException("dimension " + i + " has size 0");
      size *= d[i];
      if (size < 0)
        throw new IllegalArgumentException("array size overflow");
    }
    return size;
  }

  /*
   * Compute linear offset give the multidimensional coordinate.
   * Coordinate is specified as an array of integers. For example,
   * [ 1, 2, 3 ] denotes the element at [1][2][3].
   *
   * Based on https://en.wikipedia.org/wiki/Row-_and_column-major_order
   * row major order formula. As an example,
   *
   * - given an array [N1][N2][N3][N4]
   * - given the coordinate [n1][n2][n3][n4]
   *
   * The linear offset is computed as,
   *
   *      n1 x N2 x N3 x N4 + n2 x N3 x N4 + n3 x N4 + n4
   *
   * This can be rewritten as,
   *
   *      ((n1 x N2 + n2) x N3 + n3) x N4 + n4
   *
   * This is the formula used for the for loop.
   *
   * Model compiler guarantees coordinates are within bounds so
   * no need to check here.
   */
  private int idx(int[] coord) {
    /*
    if (coord.length != dims.length)
        throw new IllegalArgumentException(
                "coord dimensions=" + coord.length + " != array dimensions=" + dims.length);
    if (coord[0] >= dims[0])
        throw new ArrayIndexOutOfBoundsException("coord[" + coord[0] + "]");
    */
    int idx = coord[0];
    for (int i = 1; i < dims.length; i++) {
      /*
      if (coord[i] >= dims[i])
          throw new ArrayIndexOutOfBoundsException("coord[" + coord[i] + "]");
      */
      idx = idx * dims[i] + coord[i];
    }
    return offset+idx;
  }

  /*
   * Return the value at the specified coordinate.
   */
  public boolean get(int[] coord) {
    return data[idx(coord)] != 0;
  }

  /*
   * Return the value at the specified linear index.
   */
  public boolean get(int idx) {
    return data[offset+idx] != 0;
  }

  /*
   * Set the value at the specified coordinate.
   */
  public void set(int[] coord, boolean b) {
    data[idx(coord)] = (b ? (byte)1 : (byte)0);
  }

  /*
   * Set the value at the specified linear index.
   */
  public void set(int idx, boolean b) {
    data[offset+idx] = (b ? (byte)1 : (byte)0);
  }

  /*
   * Return the raw data array (for loading weights).
   */
  public byte[] raw() {
    return data;
  }

  /*
   * Return the one-dimensional data array.
   */
  public byte[] data() {
    return Arrays.copyOfRange(data, offset, offset + size);
  }

  /*
   * Return the dimensions of the array.
   */
  public int[] dim() {
    return dims;
  }

  /*
   * Return the size of a specific dimension.
   */
  public int dim(int i) {
    return dims[i];
  }

  /*
   * Return the total size of the array.
   */
  public int size() {
    return size;
  }

  /*
   * Share the underlying data array with another byte[], starting
   * at the offset 0.
   */
  public void share(byte[] b) {
    share(b, 0);
  }

  /*
   * Share the underlying data array with another byte[], starting
   * at the offset n into b.
   */
  public void share(byte[] b, int n) {
    if (n + size < 0)
      throw new IllegalArgumentException("offset + array size overflow");
    if (n + size > b.length)
      throw new IllegalArgumentException("offset + array size out of bound");
    data = b;
    offset = n;
  }

  /*
   * Share the underlying data array with another Cbool, starting
   * at the offset 0.
   */
  public void share(Cbool b) {
    share(b, 0);
  }

  /*
   * Share the underlying data array with another Cbool, starting
   * at the offset n into b.
   */
  public void share(Cbool b, int n) {
    if (n + size < 0)
      throw new IllegalArgumentException("offset + array size overflow");
    if (n + size > b.size())
      throw new IllegalArgumentException("offset + array size out of bound");
    data = b.data;
    offset = n + b.offset;
  }

  /*
   * Invoked by print() to recursively print the array.
   */
  private void print2(int[] dims, int dim, int[] idx, String out) {
    if (dim == dims.length - 1) {
      for (int i = 0; i < dims[dim]; i++)
        System.out.println(out + "[" + i + "]=" + data[idx[0]++]);
    }
    else {
      for (int i = 0; i < dims[dim]; i++)
        print2(dims, dim+1, idx, out + "[" + i + "]");
    }
  }

  /*
   * Print the array according to the dimensions specified in dims.
   * Prefix each element with "name". Note our data start at 'offset'
   * in the data array.
   */
  public void print(String name) {
    print2(dims, 0, new int[]{offset}, name);
  }

  /*
   * Print the array without name prefix.
   */
  public void print() {
    print("");
  }
}
