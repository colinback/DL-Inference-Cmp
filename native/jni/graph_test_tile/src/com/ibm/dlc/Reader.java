package com.ibm.dlc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.jar.JarFile;

public class Reader {

  // Convert string endian into ByteOrder endian constant.
  public static ByteOrder GetEndian(String endian) {
    if (endian.equalsIgnoreCase("b")) {
      return ByteOrder.BIG_ENDIAN;
    }
    else if (endian.equalsIgnoreCase("l")) {
      return ByteOrder.LITTLE_ENDIAN;
    }
    else {
      throw new IllegalArgumentException("Unknown endian type " + endian);
    }
  }

  // Return an InputStream associated with a file inside jar.
  private static InputStream GetJarFileInputStream(String path) throws Exception {
    File jar = new File(DLC.class.getProtectionDomain()
                                 .getCodeSource()
                                 .getLocation().toURI());
    JarFile jf = new JarFile(jar);
    return jf.getInputStream(jf.getEntry(path));
  }

  // Check if a file exists inside jar.
  public static boolean ExistInsideJar(String path) throws Exception {
    File jar = new File(DLC.class.getProtectionDomain()
                                 .getCodeSource()
                                 .getLocation().toURI());
    JarFile jf = new JarFile(jar);
    return jf.getEntry(path) != null;
  }

  // Convenient methods for Read_float.
  public static float[] Read_float(String path) throws Exception {
    return Read_float(path, ByteOrder.LITTLE_ENDIAN, false);
  }

  public static float[] Read_float(String path, ByteOrder endian) throws Exception {
    return Read_float(path, endian, false);
  }

  public static float[] Read_float(String path, boolean jar) throws Exception {
    return Read_float(path, ByteOrder.LITTLE_ENDIAN, jar);
  }

  // Read binary file with specified endian into float[] array.
  // If jar == true, path refers to a file inside a jar file.
  public static float[] Read_float(String path, ByteOrder endian, boolean jar) throws Exception {
    final InputStream is = jar ? GetJarFileInputStream(path) :
                                 new FileInputStream(path);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final byte[] buf = new byte[Math.min(Integer.MAX_VALUE, is.available())];
    for (int n; (n = is.read(buf, 0, buf.length)) > 0;) {
      baos.write(buf, 0, n);
    }
    is.close();

    final FloatBuffer fb = ByteBuffer.wrap(baos.toByteArray())
                                     .order(endian)
                                     .asFloatBuffer();
    final float[] floats = new float[fb.limit()];
    fb.get(floats);
    return floats;
  }

  // Convenient methods for Read_int32_t.
  public static int[] Read_int32_t(String path) throws Exception {
    return Read_int32_t(path, ByteOrder.LITTLE_ENDIAN, false);
  }

  public static int[] Read_int32_t(String path, ByteOrder endian) throws Exception {
    return Read_int32_t(path, endian, false);
  }

  public static int[] Read_int32_t(String path, boolean jar) throws Exception {
    return Read_int32_t(path, ByteOrder.LITTLE_ENDIAN, jar);
  }

  // Read binary file with specified endian into int[] array.
  // If jar == true, path refers to a file inside a jar file.
  public static int[] Read_int32_t(String path, ByteOrder endian, Boolean jar) throws Exception {
    final InputStream is = jar ? GetJarFileInputStream(path) :
                                 new FileInputStream(path);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final byte[] buf = new byte[Math.min(Integer.MAX_VALUE, is.available())];
    for (int n; (n = is.read(buf, 0, buf.length)) > 0;) {
      baos.write(buf, 0, n);
    }
    is.close();

    final IntBuffer ib = ByteBuffer.wrap(baos.toByteArray())
                                   .order(endian)
                                   .asIntBuffer();
    final int[] ints = new int[ib.limit()];
    ib.get(ints);
    return ints;
  }

  // Convenient methods for Read_int64_t.
  public static long[] Read_int64_t(String path) throws Exception {
    return Read_int64_t(path, ByteOrder.LITTLE_ENDIAN, false);
  }

  public static long[] Read_int64_t(String path, ByteOrder endian) throws Exception {
    return Read_int64_t(path, endian, false);
  }

  public static long[] Read_int64_t(String path, boolean jar) throws Exception {
    return Read_int64_t(path, ByteOrder.LITTLE_ENDIAN, jar);
  }

  // Read binary file with specified endian into long[] array.
  // If jar == true, path refers to a file inside a jar file.
  public static long[] Read_int64_t(String path, ByteOrder endian, Boolean jar) throws Exception {
    final InputStream is = jar ? GetJarFileInputStream(path) :
                                 new FileInputStream(path);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final byte[] buf = new byte[Math.min(Integer.MAX_VALUE, is.available())];
    for (int n; (n = is.read(buf, 0, buf.length)) > 0;) {
      baos.write(buf, 0, n);
    }
    is.close();

    final LongBuffer lb = ByteBuffer.wrap(baos.toByteArray())
                                    .order(endian)
                                    .asLongBuffer();
    final long[] longs = new long[lb.limit()];
    lb.get(longs);
    return longs;
  }

  // Convenient methods for Read_bool.
  public static byte[] Read_bool(String path) throws Exception {
    return Read_bool(path, ByteOrder.LITTLE_ENDIAN, false);
  }

  public static byte[] Read_bool(String path, ByteOrder endian) throws Exception {
    return Read_bool(path, endian, false);
  }

  public static byte[] Read_bool(String path, boolean jar) throws Exception {
    return Read_bool(path, ByteOrder.LITTLE_ENDIAN, jar);
  }

  // Read binary file with specified endian into byte[] array.
  // If jar == true, path refers to a file inside a jar file.
  // Note that endian argument is ignored but kept for consistency.
  public static byte[] Read_bool(String path, ByteOrder endian, Boolean jar) throws Exception {
    final InputStream is = jar ? GetJarFileInputStream(path) :
                                 new FileInputStream(path);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final byte[] buf = new byte[Math.min(Integer.MAX_VALUE, is.available())];
    for (int n; (n = is.read(buf, 0, buf.length)) > 0;) {
      baos.write(buf, 0, n);
    }
    is.close();

    return baos.toByteArray();
  }
}
