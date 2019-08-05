# DLC-PER

## Prequisites

```shell
$ sudo apt install unzip apache2-utils zlib1g-dev libpng-dev python3-pip docker.io
$ pip3 install tensorflow onnx tf2onnx onnxruntime pytest pytest-benchmark
```

Follow the [bazel install guide](https://docs.bazel.build/versions/master/install-ubuntu.html) to install bazel 0.25.2

```shell
$ wget https://github.com/bazelbuild/bazel/releases/download/0.25.2/bazel-0.25.2-installer-linux-x86_64.sh
$ bash bazel-0.25.2-installer-linux-x86_64.sh
$ source /usr/local/lib/bazel/bin/bazel-complete.bash
```

## Clone project

```shell
$ git clone https://github.com/colinback/DL-Inference-Cmp.git
$ cd DLC-PER

# set RPOJECT env
$ PROJECT=$(pwd)
```

## Create LSTM model (.pb & .onnx)

```shell
# create lstm model and save
$ python3 create_and_save.py

# compare prediction result between onnx model and tf model
$ python3 load_and_predict.py
```

## Tensorflow Model Benchmark

1. Deploy Tensorflow Model in tf-serving

```shell
# deploy
$ docker pull tensorflow/serving
$ cd $PROJECT/tf-serving
$ sh tf-serving-deploy.sh

# apache benchmark
$ cd $PROJECT/tf-serving/client-ab
$ sh lstm_ab.sh 1 1000

# c++ benchmark
$ pip3 install future
$ ln -s /usr/bin/python3 /usr/bin/python

$ cd $PROJECT/tf-serving/client-grpc
$ bazel build --define=grpc_no_ares=true :lstm_client_cc
$ bazel-bin/lstm_client_cc --benchmark_repetitions=10
```

2. Predict Tensorflow Model in tensorflow

```shell
# python API
$ cd $PROJECT/tensorflow/client-python
$ py.test test_tf.py
```

## ONNX Benchmark

1. Deploy ONNX Model in onnxruntime server (prediction fails)

```shell
#deploy
$ docker pull mcr.microsoft.com/onnxruntime/server
$ cd $PROJECT/onnxrt-server
$ sh onnxrt-server-deploy.sh

# apache benchmark
$ cd $PROJECT/onnxrt-server/client-ab
$ sh lstm_ab.sh 1 1000
```

2. Predict ONNX Model in onnxruntime

```shell
# python API
$ cd $PROJECT/onnxruntime/client-python
$ py.test test_onnx.py

# c++ API (it requres cmake 3.13 or higher)
$ cd ~
$ wget https://github.com/Kitware/CMake/releases/download/v3.14.6/cmake-3.14.6.tar.gz
$ tar -zxvf cmake-3.14.6.tar.gz
$ cd cmake-3.14.6/
$ ./configure
$ make && make install

$ cd $PROJECT
$ git submodule update --init --recursive

$ cd $PROJECT/onnxruntime/client-cc
$ mkdir build && cd build
$ cmake ..
$ make -j8
$ ./lstm_client --benchmark_repetitions=10
```
