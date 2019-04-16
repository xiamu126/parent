#!/bin/bash
docker build -t nginx-znld:v1 .
docker container stop $(docker container ls -f "name=nginx-znld" -q)
docker container rm $(docker container ls -a -f "name=nginx-znld" -q)
docker image rm $(docker image ls -f "dangling=true" -q)
docker run -d -p 9090:80 --name nginx-znld nginx-znld:v1

#dos2unix ./nginx_cmd.sh
