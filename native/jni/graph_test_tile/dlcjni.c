#include <stdbool.h>
#include "jni.h"

typedef bool byte;

// Prototype of main function to call to run the computation graph.
void prepare(void *env);
void** run(float (*t1)[8][6][2]);
void finalize(void *env);

// JNI C wrapper function called by the Java code.
JNIEXPORT jobjectArray JNICALL Java_com_ibm_dlc_DLC_c_1run_1wrapper(JNIEnv *env, jclass cls, jfloatArray jt1) {
  // Compute derived parameters.
  
  // Get the array pointer from the Java array object.
  float (*t1)[8][6][2] = (float (*)[8][6][2])((*env)->GetFloatArrayElements(env, jt1, 0));

  // Call the main function in library.c.
  prepare(env);
  void **out_feature = run(t1);

  // Return tensors in an array of flattened objects.
  char __c[] = "[F";
#ifdef __MVS__
  // z/OS xlc encodes in EBCDIC but Java expects ASCII.
  __e2a_s(__c);
#endif
  jclass __jc = (*env)->FindClass(env, __c);
  jobjectArray __jret = (*env)->NewObjectArray(env, 1, __jc, 0);

  // Copy out_feature into Java array and set __jret element.
  jfloatArray __jconcat_2 = (*env)->NewFloatArray(env, 64*24*6);
  (*env)->SetFloatArrayRegion(env, __jconcat_2, 0, 64*24*6, (jfloat *)out_feature[0]);
  (*env)->SetObjectArrayElement(env, __jret, 0, __jconcat_2);

  // Release local reference to the Java array object.
  (*env)->ReleaseFloatArrayElements(env, jt1, (jfloat *)t1, 0);

  finalize(env);
  return __jret;
} //Java_com_ibm_dlc_DLC_c_1run_1wrapper
