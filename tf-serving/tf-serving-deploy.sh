#!/bin/bash

PDIR=`dirname "$(pwd)"`
docker run -t --rm -p 8501:8501 -p 8500:8500 \
    -v "${PDIR}/models/lstm:/models/lstm" \
    -e MODEL_NAME=lstm \
    tensorflow/serving &