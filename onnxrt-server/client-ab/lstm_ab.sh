#!/bin/bash

concurrency_degree=$1
total_request_num=$2

INPUT="./input.json"
IP=http://localhost
PORT=8001
MODEL=lstm

ab -v 1 -p $INPUT -T 'application/json' -H 'Cache-Control:no-cache' -k -c $concurrency_degree -n $total_request_num -r -s 300 $IP:$PORT/v1/models/$MODEL/versions/1:predict
echo $MODEL evaluated