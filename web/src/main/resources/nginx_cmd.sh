#!/bin/bash
docker build -t nginx-cmd:v1 .
docker container stop $(docker container ls -f "name=nginx-cmd" -q)
docker container rm $(docker container ls -a -f "name=nginx-cmd" -q)
docker image rm $(docker image ls -f "dangling=true" -q)
docker run -d -p 9292:80 --name nginx-cmd nginx-cmd:v1

#dos2unix ./nginx_cmd.sh
