#!/usr/bin/env bash
name_prefix="redis-"

if [[ $# -lt 2 ]]
then
    echo "usage: `basename $0` port config_path"
    exit 1
fi

if [[ -z "`echo $1 | sed -n '/^[1-9][0-9]*$/p'`" || ! -f $2 ]]; then
        echo -e "usage: `basename $0` port config_path, port should be number"
        exit 1
fi

[[ -z "`docker images | sed -n '/redis/p'`" ]] && docker pull redis:latest
if [[ "`docker ps -aq | wc -l`" -gt 0 ]]; then
    docker_id=`docker ps -a | sed -n "/${name_prefix}$1$/p" | cut -f1 -d " "`
    [[ -n "${docker_id}" ]] && docker stop "${docker_id}" >> /dev/null && echo "stop ${docker_id}"
fi

echo "${name_prefix}$1"
docker run -d --rm --restart=always \
    -p $1:6379 --name "${name_prefix}$1" \
    -v $2:/usr/local/etc/redis/redis.conf:z redis redis-server /usr/local/etc/redis/redis.conf