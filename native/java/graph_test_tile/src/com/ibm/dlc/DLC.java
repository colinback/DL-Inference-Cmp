package com.ibm.dlc;

import java.net.URL;
import java.nio.ByteOrder;
import java.util.Arrays;

public class DLC {

  // For testing to read a weights file in the jar file when --debug
  // is not set so TestDriver.java is not generated.
  // You need to specify the type of the weights, i.e., ft, 32, 64, or bl.
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: java -cp graph.jar com.ibm.dlc.DLC ft|32|64|bl \"/weights/weights_file_name\"");
      return;
    }
    URL url = DLC.class.getResource(args[1]);
    if (url == null) {
      System.out.println("\"" + args[1] + "\" not found");
      return;
    }
    System.out.println("uri=" + url.toURI().toString());
    if (args[0].equals("ft")) {
      Cfloat f = new Cfloat(Reader.Read_float(args[1], true));
      f.print("float_weight");
    }
    else if (args[0].equals("32")) {
      Cint32_t i = new Cint32_t(null, Reader.Read_int32_t(args[1], true));
      i.print("int32_weight");
    }
    else if (args[0].equals("64")) {
      Cint64_t l = new Cint64_t(Reader.Read_int64_t(args[1], true));
      l.print("int64_weight");
    }
    else if (args[0].equals("bl")) {
      Cbool b = new Cbool(Reader.Read_bool(args[1], true));
      b.print("bool_weight");
    }
    else {
      System.out.println("Unknown weights type " + args[0]);
    }
  }

  // Check "given" dimension array is the same as the "expected".
  // Used for checking tensor input to main function.
  private static void checkInput(int[] given, int[] expected) throws Exception {
    if (given.length != expected.length)
      throw new IllegalArgumentException(
              "dimension number mismatch, given " + given.length +
              ", expected " + expected.length);

    for (int i = 0; i < given.length; i++)
      if (given[i] != expected[i])
        throw new IllegalArgumentException(
                "dimension " + i + " size mismatch, given " + given[i] +
                ", expected " + expected[i]);
  }

  // Main function to call to run the computation graph.
  // Weights are always in little endian unless forced otherwise.
  public static Object[] run(Cfloat/*[8][6][2]*/ t1) throws Exception {
    return run(t1, ByteOrder.LITTLE_ENDIAN);
  }

  // Main function to call to run the computation graph.
  // Weights endian is specified by the parameter passed in.
  public static Object[] run(Cfloat/*[8][6][2]*/ t1, ByteOrder endian) throws Exception {

    checkInput(t1.dim(), new int[]{8,6,2});

    // Intermediate buffer initialization.
    Init init = new Init(t1, endian);

    // Call computation vertices in topological order.
    Compute compute = new Compute(init);

    return compute.return_tensors;
  } // run

  // Function returning input signature as a JSON array.
  public static String inputSignature() {
    return "[{\"type\":\"Cfloat\", \"dims\":" + Arrays.toString(new int[]{8,6,2}) + "}]";
  }

  // Function returning output signature as a JSON array.
  public static String outputSignature() {
    return "[{\"type\":\"Cfloat\", \"dims\":" + Arrays.toString(new int[]{64,24,6}) + "}]";
  }
} // DLC
