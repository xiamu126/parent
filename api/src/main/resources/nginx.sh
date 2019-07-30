#!/bin/bash
if [[ $# -lt 3 ]]
then
    echo "usage: $(basename "$0") port html_path config_path"
    exit 1
fi

name=nginx_$1

[[ -z "$(docker-build images | sed -n '/nginx/p')" ]] && docker pull nginx:latest

if [[ "`docker-build ps -aq | wc -l`" -gt 0 ]]; then
    docker_id=`docker ps -a | sed -n "/${name}$/p" | cut -f1 -d " "`
    [[ -n "${docker_id}" ]] && docker stop ${docker_id}
fi

#-v $(pwd)/logs/nginx:/var/log/nginx
docker run -d --name ${name} -p $1:80 --privileged=true --restart=always\
        -v $2:/usr/share/nginx/html:ro \
        -v $3:/etc/nginx/nginx.conf:ro nginx

