// Main function to call to run the computation graph.
#include "library.h"
#include "tensor.h"
void prepare(void *env) {
// Constant-shaped tensor storage initialization.
 __dlc_ts_concat = malloc( 64 * 6 * 2 * sizeof(float)); 
 __dlc_ts_concat_1 = malloc( 64 * 24 * 2 * sizeof(float)); 
 __dlc_ts_concat_2 = malloc( 64 * 24 * 6 * sizeof(float)); 
 __dlc_ts_repeats = malloc( 3 * sizeof(int32_t)); 
__dlc_ts_repeats_alias = ReadTensor(env, "graph_weights/repeats.w", "int32_t", &__dlc_ts_repeats);

 } 

void** run(float *t1) {
// Compute derived parameters.
// Initialize non-constant tensor storage for inference.
float (*concat)[64][6][2] = (float(*)[64][6][2])__dlc_ts_concat;
float (*concat_1)[64][24][2] = (float(*)[64][24][2])__dlc_ts_concat_1;
float (*concat_2)[64][24][6] = (float(*)[64][24][6])__dlc_ts_concat_2;
int32_t (*repeats)[3] = (int32_t(*)[3])__dlc_ts_repeats;
// Call computation vertices in topological order.
compute__concat_concat_1_concat_2( t1, concat, concat_1, concat_2);
// Return tensors in one array of pointers:
void** return_tensors = (void**) malloc(1*sizeof(void*));
return_tensors[0] = (void*)concat_2;

return (void**)return_tensors;
}
void finalize(void *env) {
free(__dlc_ts_concat);
free(__dlc_ts_concat_1);
free(__dlc_ts_concat_2);
ReleaseTensor(env, "int32_t", __dlc_ts_repeats, __dlc_ts_repeats_alias);
}
