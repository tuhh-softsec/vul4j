#!/bin/bash
echo "========build project"
./mvnw clean install -Dskiptests=true

echo "===========build docker image"
docker build -t patientvn/pm-patient-service:v1 .

echo "=========== push dockerimage"
docker push patientvn/pm-patient-service:v1
