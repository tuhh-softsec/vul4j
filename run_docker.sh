#!/bin/bash

docker run -d -it \
  --name vul4j \
  -v /root/.m2/repository:/root/.m2/repository \
  bqcuongas/vul4j