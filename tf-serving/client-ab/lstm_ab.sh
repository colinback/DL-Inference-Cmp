#!/bin/bash

# Server Software:
# Server Hostname:        localhost
# Server Port:            8501

# Document Path:          /v1/models/lstm:predict
# Document Length:        746436 bytes

# Concurrency Level:      1
# Time taken for tests:   70.717 seconds
# Complete requests:      2000
# Failed requests:        0
# Keep-Alive requests:    2000
# Total transferred:      1493070000 bytes
# Total body sent:        41434000
# HTML transferred:       1492872000 bytes
# Requests per second:    28.28 [#/sec] (mean)
# Time per request:       35.358 [ms] (mean)
# Time per request:       35.358 [ms] (mean, across all concurrent requests)
# Transfer rate:          20618.57 [Kbytes/sec] received
#                         572.18 kb/s sent
#                         21190.75 kb/s total

concurrency_degree=$1
total_request_num=$2

INPUT="./input.json"
IP=http://localhost
PORT=8501
MODEL=lstm

ab -v 1 -p $INPUT -T 'application/json' -H 'Cache-Control:no-cache' -k -c $concurrency_degree -n $total_request_num -r -s 300 $IP:$PORT/v1/models/$MODEL:predict
echo $MODEL evaluated