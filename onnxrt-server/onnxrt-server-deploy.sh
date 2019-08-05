#!/bin/bash

PDIR=`dirname "$(pwd)"`
docker run -t --rm -p 8001:8001 \
    -v "${PDIR}/models:/models" \
    -e MODEL_ABSOLUTE_PATH=/models/lstm.onnx \
    mcr.microsoft.com/onnxruntime/server &