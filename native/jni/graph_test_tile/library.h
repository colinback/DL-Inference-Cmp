
#include "omp.h"
#include "stdbool.h"
#include "stdint.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>

#include "runtime_jni.h"

#include "compute__concat_concat_1_concat_2/compute__concat_concat_1_concat_2.h"
void prepare(void *env);
void** run(
float *t1);
void finalize(void *env);
void* __dlc_ts_concat;
void* __dlc_ts_concat_1;
void* __dlc_ts_concat_2;
void* __dlc_ts_repeats;
void *__dlc_ts_repeats_alias;
