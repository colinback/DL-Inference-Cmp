// Copyright(c) Microsoft Corporation.All rights reserved.
// Licensed under the MIT License.
//

#include <assert.h>
#include <vector>
#include <onnxruntime/core/session/onnxruntime_cxx_api.h>
#include <benchmark/benchmark.h>

static void BM_Predict(benchmark::State& state) {
  // Perform setup here

  //*************************************************************************
  // initialize  enviroment...one enviroment per process
  // enviroment maintains thread pools and other state info
  Ort::Env env(ORT_LOGGING_LEVEL_WARNING, "test");

  // initialize session options if needed
  Ort::SessionOptions session_options;
  session_options.SetThreadPoolSize(1);

  // If onnxruntime.dll is built with CUDA enabled, we can uncomment out this line to use CUDA for this
  // session (we also need to include cuda_provider_factory.h above which defines it)
  // #include "cuda_provider_factory.h"
  // OrtSessionOptionsAppendExecutionProvider_CUDA(session_options, 1);

  // Sets graph optimization level
  // Available levels are
  // 0 -> To disable all optimizations
  // 1 -> To enable basic optimizations (Such as redundant node removals)
  // 2 -> To enable all optimizations (Includes level 1 + more complex optimizations like node fusions)
  session_options.SetGraphOptimizationLevel(1);

  //*************************************************************************
  // create session and load model into memory
  // using squeezenet version 1.3
  // URL = https://github.com/onnx/models/tree/master/squeezenet
#ifdef _WIN32
  const wchar_t* model_path = L"../../../models/lstm.onnx";
#else
  const char* model_path = "../../../models/lstm.onnx";
#endif

  // printf("Using Onnxruntime C++ API\n");
  Ort::Session session(env, model_path, session_options);

  //*************************************************************************
  // print model input layer (node names, types, shape etc.)
  Ort::Allocator allocator = Ort::Allocator::CreateDefault();

  // print number of model input nodes
  size_t num_input_nodes = session.GetInputCount();
  std::vector<const char*> input_node_names(num_input_nodes);
  std::vector<int64_t> input_node_dims;  // simplify... this model has only 1 input node {16, 10, 512}.
                                         // Otherwise need vector<vector<>>

  // printf("Number of inputs = %zu\n", num_input_nodes);

  // iterate over all input nodes
  for (int i = 0; i < num_input_nodes; i++) {
    // print input node names
    char* input_name = session.GetInputName(i, allocator);
    input_node_names[i] = input_name;

    // input node types
    Ort::TypeInfo type_info = session.GetInputTypeInfo(i);
    auto tensor_info = type_info.GetTensorTypeAndShapeInfo();

    // input shapes/dims
    input_node_dims = tensor_info.GetShape();
  }

  //*************************************************************************
  // Similar operations to get output node information.
  // Use OrtSessionGetOutputCount(), OrtSessionGetOutputName()
  // OrtSessionGetOutputTypeInfo() as shown above.

  //*************************************************************************
  // Score the model using sample data, and inspect values

  size_t input_tensor_size = 16 * 10 * 512;  // simplify ... using known dim values to calculate size
                                             // use OrtGetTensorShapeElementCount() to get official size!

  std::vector<float> input_tensor_values(input_tensor_size);
  std::vector<const char*> output_node_names = {"output_1:0"};

  // initialize input data with values in [0.0, 1.0]
  for (unsigned int i = 0; i < input_tensor_size; i++)
    input_tensor_values[i] = (float)i / (input_tensor_size + 1);

  // create input tensor object from data values
  Ort::AllocatorInfo allocator_info = Ort::AllocatorInfo::CreateCpu(OrtArenaAllocator, OrtMemTypeDefault);
  Ort::Value input_tensor = Ort::Value::CreateTensor<float>(allocator_info, input_tensor_values.data(), input_tensor_size, input_node_dims.data(), 3);
  assert(input_tensor.IsTensor());

  for (auto _ : state) {
    // This code gets timed
    auto output_tensors = session.Run(Ort::RunOptions{nullptr}, input_node_names.data(), &input_tensor, 1, output_node_names.data(), 1);
    assert(output_tensors.size() == 1 && output_tensors.front().IsTensor());
  }
}

// Register the function as a benchmark
BENCHMARK(BM_Predict);

// Run the benchmark
BENCHMARK_MAIN();