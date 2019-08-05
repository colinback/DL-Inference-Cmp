package com.ibm.dlc;

public class Compute {
  Object[] return_tensors;

  private interface _Compute {
    void _compute();
  }

  /*
   * Java Math class has no erf/erfc, the following is converted from
   * Cephes Mathematical Library C source code.
   * https://www.netlib.org/cephes
   */
  private static final double MAXLOG = 7.09782712893383996732E2;

  private static final double P[] = {
    2.46196981473530512524E-10,
    5.64189564831068821977E-1,
    7.46321056442269912687E0,
    4.86371970985681366614E1,
    1.96520832956077098242E2,
    5.26445194995477358631E2,
    9.34528527171957607540E2,
    1.02755188689515710272E3,
    5.57535335369399327526E2
  };

  private static final double Q[] = {
    /*1.00000000000000000000E0,*/
    1.32281951154744992508E1,
    8.67072140885989742329E1,
    3.54937778887819891062E2,
    9.75708501743205489753E2,
    1.82390916687909736289E3,
    2.24633760818710981792E3,
    1.65666309194161350182E3,
    5.57535340817727675546E2
  };

  private static final double R[] = {
    5.64189583547755073984E-1,
    1.27536670759978104416E0,
    5.01905042251180477414E0,
    6.16021097993053585195E0,
    7.40974269950448939160E0,
    2.97886665372100240670E0
  };

  private static final double S[] = {
    /*1.00000000000000000000E0,*/
    2.26052863220117276590E0,
    9.39603524938001434673E0,
    1.20489539808096656605E1,
    1.70814450747565897222E1,
    9.60896809063285878198E0,
    3.36907645100081516050E0
  };

  private static final double T[] = {
    9.60497373987051638749E0,
    9.00260197203842689217E1,
    2.23200534594684319226E3,
    7.00332514112805075473E3,
    5.55923013010394962768E4
  };

  private static final double U[] = {
    /*1.00000000000000000000E0,*/
    3.35617141647503099647E1,
    5.21357949780152679795E2,
    4.59432382970980127987E3,
    2.26290000613890934246E4,
    4.92673942608635921086E4
  };

  static public double erfc(double a) throws ArithmeticException {
    double x, y, z, p, q;

    if ((x = a < 0.0 ? -a : a) < 1.0) return 1.0 - erf(a);
    if ((z = -a * a) < -MAXLOG) {
      throw new ArithmeticException("erfc underflow");
      /* return a < 0.0 ? 2.0 : 0.0; */
    }

    z = Math.exp(z);
    p = x < 8.0 ? polevl(x, P, 8) : polevl(x, R, 5);
    q = x < 8.0 ? p1evl(x, Q, 8)  : p1evl(x, S, 6);
    y = (z * p) / q;
    if ((y = a < 0.0 ? 2.0 - y : y) == 0.0) {
      throw new ArithmeticException("erfc underflow");
      /* return a < 0.0 ? 2.0 : 0.0; */
    }
    return y;
  }

  static public double erf(double x) throws ArithmeticException {
    double y;

    if (Math.abs(x) > 1.0) return 1.0 - erfc(x);
    y = x * x;
    return x * polevl(y, T, 4) / p1evl(y, U, 5);
  }

  static private double polevl(double x, double coef[], int N) {
    double ans;

    ans = coef[0];
    for (int i = 1; i <= N; i++) {
      ans = ans * x + coef[i];
    }
    return ans;
  }

  static private double p1evl(double x, double coef[], int N) {
    double ans;

    ans = x + coef[0];
    for (int i = 1; i < N; i++) {
      ans = ans * x + coef[i];
    }
    return ans;
  }

  // Java Math class has no asinh, acosh, atanh so roll our own.
  public static double asinh(double x)
  {
    return Math.log(x + Math.sqrt(x*x + 1.0));
  }

  public static double acosh(double x)
  {
    return Math.log(x + Math.sqrt(x*x - 1.0));
  }

  public static double atanh(double x)
  {
    return 0.5 * Math.log((1.0 + x) / (1.0 - x));
  }

  public Compute(Init init) {
    _Compute[] lambda = {
      () -> {
        Compute__concat_concat_1_concat_2.compute__concat_concat_1_concat_2(init.t1, init.concat, init.concat_1, init.concat_2);
      }
    };
    for (int i = 0; i < lambda.length; i++) lambda[i]._compute();

    // Return tensors in an array of objects.
    return_tensors = new Object[1];
    return_tensors[0] = init.concat_2;
  }
}
