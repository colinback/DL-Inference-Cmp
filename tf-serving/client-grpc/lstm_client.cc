#include <fstream>
#include <iostream>

#include "grpcpp/create_channel.h"
#include "grpcpp/security/credentials.h"
#include "google/protobuf/map.h"
#include "tensorflow/core/framework/tensor.h"
#include "tensorflow/core/platform/types.h"
#include "tensorflow/core/util/command_line_flags.h"
#include "tensorflow_serving/apis/prediction_service.grpc.pb.h"

#include <benchmark/benchmark.h>

using grpc::Channel;
using grpc::ClientContext;
using grpc::Status;

using tensorflow::serving::PredictRequest;
using tensorflow::serving::PredictResponse;
using tensorflow::serving::PredictionService;

typedef google::protobuf::Map<tensorflow::string, tensorflow::TensorProto> OutMap;

static void BM_Predict(benchmark::State& state) {
  // Perform setup here
  size_t input_tensor_size = 16 * 10 * 512;
  std::vector<float> input_tensor_values(input_tensor_size);

  // initialize input data with values in [0.0, 1.0]
  for (unsigned int i = 0; i < input_tensor_size; i++)
    input_tensor_values[i] = (float)i / (input_tensor_size + 1);

  tensorflow::string server_port = "localhost:8500";
  tensorflow::string model_name = "lstm";
  tensorflow::string model_signature_name = "serving_default";

  std::shared_ptr<Channel> channel = grpc::CreateChannel(server_port, grpc::InsecureChannelCredentials());
  std::shared_ptr<PredictionService::Stub> stub = PredictionService::NewStub(channel);

  std::shared_ptr<PredictRequest> predictRequest(new PredictRequest());

  predictRequest->mutable_model_spec()->set_name(model_name);
  predictRequest->mutable_model_spec()->set_signature_name(model_signature_name);

  google::protobuf::Map<tensorflow::string, tensorflow::TensorProto>& inputs =
      *predictRequest->mutable_inputs();

  tensorflow::TensorProto proto;

  proto.set_dtype(tensorflow::DataType::DT_FLOAT);
  for(int i = 0; i < input_tensor_size; i++)
      proto.add_float_val(input_tensor_values[i]);

  proto.mutable_tensor_shape()->add_dim()->set_size(16);
  proto.mutable_tensor_shape()->add_dim()->set_size(10);
  proto.mutable_tensor_shape()->add_dim()->set_size(512);

  inputs["input_1"] = proto;
  
  for (auto _ : state) {
    ClientContext context;
    PredictResponse response;
    // This code gets timed
    stub->Predict(&context, *predictRequest, &response);
  }
}

// Register the function as a benchmark
BENCHMARK(BM_Predict);

// Run the benchmark
BENCHMARK_MAIN();