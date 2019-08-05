package com.ibm.dlc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.jar.JarFile;

public class DLC
{
  static String libname = "libgraph_native.so";
  static String weights = "graph_weights";

  static {
    File jar;
    JarFile jf;
    String jarPath = null;
    File tmplib = new File(System.getProperty("java.io.tmpdir"), libname);
    try {
      // Get path name of jar
      jar = new File(DLC.class.getProtectionDomain()
                              .getCodeSource()
                              .getLocation().toURI());
      jarPath = jar.getParentFile().getAbsolutePath();

      // Open jar file to read and check libname and weights inside jar.
      // If IOException thrown, load .so from where .jar is.
      //
      // Checking whether DLC.class.getResourceAsStream returns null
      // does NOT work. Because it checks whether the resource is
      // available on the classpath, not only just inside the jar file.
      jf = new JarFile(jar);
      if (jf.getEntry(libname) != null && jf.getEntry(weights) != null) {
        // Copy .so to java.io.tmpdir
        Files.copy(jf.getInputStream(jf.getEntry(libname)),
                   tmplib.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Load the temporary .so copy
        System.load(tmplib.getAbsolutePath());
      }
      else {
        // Throw subclass of IOException
        throw new FileNotFoundException(".so and/or weights dir not found inside jar");
      }
    } catch (URISyntaxException e) {
      // Load the .so from cwd
      System.load(libname);
    } catch (IOException e) {
      // Load the .so from where .jar is
      System.load(jarPath + "/" + libname);
    } finally {
      // POSIX can unlink file after loading
      tmplib.delete();
    }
  }

  // Prototype of JNI wrapper function to call.
  private static native Object[] c_run_wrapper(float[/*8*6*2*/] t1);

  // Java main function to call to run the computation graph.
  public static Object[] run(float[/*8*6*2*/] t1) throws Exception {

    // Compute derived parameters.
    
    // Check input length matching expected dimensions.
    if (t1.length != 8*6*2)
      throw new IllegalArgumentException(
            "t1.length=" + t1.length + " != 8*6*2");

    // Call JNI wrapper function.
    Object[] cret = c_run_wrapper(t1);

    // Return tensors in an array of unflattened objects.
    Object[] jret = new Object[1];

    jret[0] = new float[64][24][6];
    Dim.unflatten(jret[0], Dim.dim(jret[0], true), 0, cret[0], new int[]{0});

    return jret;
  } // run

  // Function returning input signature as a JSON array.
  public static String inputSignature() {
    return "[{\"type\":\"float\", \"dims\":" + Arrays.toString(new int[]{8*6*2}) + "}]";
  }

  // Function returning output signature as a JSON array.
  public static String outputSignature() {
    // Compute derived parameters.
    
    return "[{\"type\":\"float\", \"dims\":" + Arrays.toString(new int[]{64*24*6}) + "}]";
  }
} // DLC
