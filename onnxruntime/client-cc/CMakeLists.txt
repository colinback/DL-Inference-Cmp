cmake_minimum_required(VERSION 3.3)
cmake_policy(SET CMP0023 NEW)
project(onnx-cc)

option(onnxruntime_BUILD_UNIT_TESTS "Build ONNXRuntime unit tests" OFF)
option(BENCHMARK_ENABLE_TESTING "Enable testing of the benchmark library." OFF)

include_directories(third_party/onnxruntime/include)
add_subdirectory(third_party/onnxruntime/cmake)

include_directories(third_party/benchmark/include)
add_subdirectory(third_party/benchmark)

# Set CMAKE_CXX_STANDARD.
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wl -march=native -ldl")

find_package(Threads REQUIRED)

add_executable(lstm_client lstm_client.cpp)
target_link_libraries(lstm_client
                      onnxruntime_session
                      onnxruntime_optimizer
                      onnxruntime_providers
                      onnxruntime_util
                      onnxruntime_framework
                      onnxruntime_util
                      onnxruntime_graph
                      onnxruntime_common
                      onnxruntime_mlas
                      onnx
                      onnx_proto
                      nsync
                      gsl
                      re2
                      libprotobuf
                      benchmark
                      ${CMAKE_THREAD_LIBS_INIT})