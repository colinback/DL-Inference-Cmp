package com.ibm.dlc;

import java.nio.ByteOrder;

public class Init {
  Cfloat/*[8][6][2]*/ t1;

  Cfloat __dlc_ts_concat;
  Cfloat concat;
  Cfloat __dlc_ts_concat_1;
  Cfloat concat_1;
  Cfloat __dlc_ts_concat_2;
  Cfloat concat_2;
  Cint32_t __dlc_ts_repeats;
  Cint32_t repeats;

  private interface _Init {
    void _init() throws Exception;
  }

  public Init(Cfloat/*[8][6][2]*/ t1, ByteOrder endian) throws Exception {
    this.t1 = t1;

    _Init[] lambda = {
      () -> {
        __dlc_ts_concat = new Cfloat(new int[]{64,6,2});
        concat = new Cfloat(new int[]{64,6,2}, __dlc_ts_concat);
        __dlc_ts_concat_1 = new Cfloat(new int[]{64,24,2});
        concat_1 = new Cfloat(new int[]{64,24,2}, __dlc_ts_concat_1);
        __dlc_ts_concat_2 = new Cfloat(new int[]{64,24,6});
        concat_2 = new Cfloat(new int[]{64,24,6}, __dlc_ts_concat_2);
        __dlc_ts_repeats = new Cint32_t(new int[]{3});
        Reader.Read_int32_t(__dlc_ts_repeats, "weights/repeats.w", endian, true);
        repeats = new Cint32_t(new int[]{3}, __dlc_ts_repeats);
      }
    };
    for (int i = 0; i < lambda.length; i++) lambda[i]._init();
  }
}
