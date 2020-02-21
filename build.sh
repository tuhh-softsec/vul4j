#!/bin/bash
echo "===========build docker image"
docker build -t patientvn/pm-patient-service:v1 .

echo "=========== push dockerimage"
docker push patientvn/pm-patient-service:v1
