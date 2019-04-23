#!/bin/bash
max_ports=9
name_prefix="redis-node-"

if [[ $# -lt 3 || $# -gt max_ports ]]
then
    echo "usage: `basename $0` port1 port2 port3...port9"
    exit 1
fi

for i in $@; do
    if [[ -z "`echo ${i} | sed -n '/^[1-9][0-9]*$/p'`" ]]; then
        echo -e "usage: `basename $0` port1 port2 port3...port9, port should be number"
        exit 1
    fi
done

[[ -z "`docker-build images | sed -n '/redis/p'`" ]] && docker pull redis:latest
if [[ "`docker-build ps -aq | wc -l`" -gt 0 ]]; then
    case $# in
        3)
            docker_id1=`docker ps -a | sed -n "/${name_prefix}$1$/p" | cut -f1 -d " "`
            docker_id2=`docker ps -a | sed -n "/${name_prefix}$2$/p" | cut -f1 -d " "`
            docker_id3=`docker ps -a | sed -n "/${name_prefix}$3$/p" | cut -f1 -d " "`
            [[ -n "${docker_id1}" ]] && docker stop "${docker_id1}" >> /dev/null && echo "stop ${docker_id1}"
            [[ -n "${docker_id2}" ]] && docker stop "${docker_id2}" >> /dev/null && echo "stop ${docker_id2}"
            [[ -n "${docker_id3}" ]] && docker stop "${docker_id3}" >> /dev/null && echo "stop ${docker_id3}";;
        4)
            docker_id1=`docker ps -a | sed -n "/${name_prefix}$1$/p" | cut -f1 -d " "`
            docker_id2=`docker ps -a | sed -n "/${name_prefix}$2$/p" | cut -f1 -d " "`
            docker_id3=`docker ps -a | sed -n "/${name_prefix}$3$/p" | cut -f1 -d " "`
            docker_id4=`docker ps -a | sed -n "/${name_prefix}$4$/p" | cut -f1 -d " "`
            [[ -n "${docker_id1}" ]] && docker stop "${docker_id1}"
            [[ -n "${docker_id2}" ]] && docker stop "${docker_id2}"
            [[ -n "${docker_id3}" ]] && docker stop "${docker_id3}"
            [[ -n "${docker_id4}" ]] && docker stop "${docker_id4}";;
        5)
            docker_id1=`docker ps -a | sed -n "/${name_prefix}$1$/p" | cut -f1 -d " "`
            docker_id2=`docker ps -a | sed -n "/${name_prefix}$2$/p" | cut -f1 -d " "`
            docker_id3=`docker ps -a | sed -n "/${name_prefix}$3$/p" | cut -f1 -d " "`
            docker_id4=`docker ps -a | sed -n "/${name_prefix}$4$/p" | cut -f1 -d " "`
            docker_id5=`docker ps -a | sed -n "/${name_prefix}$5$/p" | cut -f1 -d " "`
            [[ -n "${docker_id1}" ]] && docker stop "${docker_id1}"
            [[ -n "${docker_id2}" ]] && docker stop "${docker_id2}"
            [[ -n "${docker_id3}" ]] && docker stop "${docker_id3}"
            [[ -n "${docker_id4}" ]] && docker stop "${docker_id4}"
            [[ -n "${docker_id5}" ]] && docker stop "${docker_id5}";;
        6)
            docker_id1=`docker ps -a | sed -n "/${name_prefix}$1$/p" | cut -f1 -d " "`
            docker_id2=`docker ps -a | sed -n "/${name_prefix}$2$/p" | cut -f1 -d " "`
            docker_id3=`docker ps -a | sed -n "/${name_prefix}$3$/p" | cut -f1 -d " "`
            docker_id4=`docker ps -a | sed -n "/${name_prefix}$4$/p" | cut -f1 -d " "`
            docker_id5=`docker ps -a | sed -n "/${name_prefix}$5$/p" | cut -f1 -d " "`
            docker_id6=`docker ps -a | sed -n "/${name_prefix}$6$/p" | cut -f1 -d " "`
            [[ -n "${docker_id1}" ]] && docker stop "${docker_id1}"
            [[ -n "${docker_id2}" ]] && docker stop "${docker_id2}"
            [[ -n "${docker_id3}" ]] && docker stop "${docker_id3}"
            [[ -n "${docker_id4}" ]] && docker stop "${docker_id4}"
            [[ -n "${docker_id5}" ]] && docker stop "${docker_id5}"
            [[ -n "${docker_id6}" ]] && docker stop "${docker_id6}";;
        7)
            docker_id1=`docker ps -a | sed -n "/${name_prefix}$1$/p" | cut -f1 -d " "`
            docker_id2=`docker ps -a | sed -n "/${name_prefix}$2$/p" | cut -f1 -d " "`
            docker_id3=`docker ps -a | sed -n "/${name_prefix}$3$/p" | cut -f1 -d " "`
            docker_id4=`docker ps -a | sed -n "/${name_prefix}$4$/p" | cut -f1 -d " "`
            docker_id5=`docker ps -a | sed -n "/${name_prefix}$5$/p" | cut -f1 -d " "`
            docker_id6=`docker ps -a | sed -n "/${name_prefix}$6$/p" | cut -f1 -d " "`
            docker_id7=`docker ps -a | sed -n "/${name_prefix}$7$/p" | cut -f1 -d " "`
            [[ -n "${docker_id1}" ]] && docker stop "${docker_id1}"
            [[ -n "${docker_id2}" ]] && docker stop "${docker_id2}"
            [[ -n "${docker_id3}" ]] && docker stop "${docker_id3}"
            [[ -n "${docker_id4}" ]] && docker stop "${docker_id4}"
            [[ -n "${docker_id5}" ]] && docker stop "${docker_id5}"
            [[ -n "${docker_id6}" ]] && docker stop "${docker_id6}"
            [[ -n "${docker_id7}" ]] && docker stop "${docker_id7}";;
        8)
            docker_id1=`docker ps -a | sed -n "/${name_prefix}$1$/p" | cut -f1 -d " "`
            docker_id2=`docker ps -a | sed -n "/${name_prefix}$2$/p" | cut -f1 -d " "`
            docker_id3=`docker ps -a | sed -n "/${name_prefix}$3$/p" | cut -f1 -d " "`
            docker_id4=`docker ps -a | sed -n "/${name_prefix}$4$/p" | cut -f1 -d " "`
            docker_id5=`docker ps -a | sed -n "/${name_prefix}$5$/p" | cut -f1 -d " "`
            docker_id6=`docker ps -a | sed -n "/${name_prefix}$6$/p" | cut -f1 -d " "`
            docker_id7=`docker ps -a | sed -n "/${name_prefix}$7$/p" | cut -f1 -d " "`
            docker_id8=`docker ps -a | sed -n "/${name_prefix}$8$/p" | cut -f1 -d " "`
            [[ -n "${docker_id1}" ]] && docker stop "${docker_id1}"
            [[ -n "${docker_id2}" ]] && docker stop "${docker_id2}"
            [[ -n "${docker_id3}" ]] && docker stop "${docker_id3}"
            [[ -n "${docker_id4}" ]] && docker stop "${docker_id4}"
            [[ -n "${docker_id5}" ]] && docker stop "${docker_id5}"
            [[ -n "${docker_id6}" ]] && docker stop "${docker_id6}"
            [[ -n "${docker_id7}" ]] && docker stop "${docker_id7}"
            [[ -n "${docker_id8}" ]] && docker stop "${docker_id8}";;
        9)
            docker_id1=`docker ps -a | sed -n "/${name_prefix}$1$/p" | cut -f1 -d " "`
            docker_id2=`docker ps -a | sed -n "/${name_prefix}$2$/p" | cut -f1 -d " "`
            docker_id3=`docker ps -a | sed -n "/${name_prefix}$3$/p" | cut -f1 -d " "`
            docker_id4=`docker ps -a | sed -n "/${name_prefix}$4$/p" | cut -f1 -d " "`
            docker_id5=`docker ps -a | sed -n "/${name_prefix}$5$/p" | cut -f1 -d " "`
            docker_id6=`docker ps -a | sed -n "/${name_prefix}$6$/p" | cut -f1 -d " "`
            docker_id7=`docker ps -a | sed -n "/${name_prefix}$7$/p" | cut -f1 -d " "`
            docker_id8=`docker ps -a | sed -n "/${name_prefix}$8$/p" | cut -f1 -d " "`
            docker_id9=`docker ps -a | sed -n "/${name_prefix}$9$/p" | cut -f1 -d " "`
            [[ -n "${docker_id1}" ]] && docker stop "${docker_id1}"
            [[ -n "${docker_id2}" ]] && docker stop "${docker_id2}"
            [[ -n "${docker_id3}" ]] && docker stop "${docker_id3}"
            [[ -n "${docker_id4}" ]] && docker stop "${docker_id4}"
            [[ -n "${docker_id5}" ]] && docker stop "${docker_id5}"
            [[ -n "${docker_id6}" ]] && docker stop "${docker_id6}"
            [[ -n "${docker_id7}" ]] && docker stop "${docker_id7}"
            [[ -n "${docker_id8}" ]] && docker stop "${docker_id8}"
            [[ -n "${docker_id9}" ]] && docker stop "${docker_id9}";;
    esac
fi

for i in $@; do
    echo "${name_prefix}${i}"
    docker run -d --rm --net=host --name "${name_prefix}${i}" redis redis-server \
            --port ${i} --cluster-enabled yes \
            --cluster-config-file nodes.conf \
            --cluster-node-timeout 5000
done

local_ip=`ip addr | grep ens33 | grep 'state UP' -A2 | tail -n1 | gawk '{print $2}' | cut -f1 -d '/'`
redis_dir="/application/redis/"

case $# in
3)
echo "${local_ip}:$1 $2 $3"
${redis_dir}/redis-cli --cluster create ${local_ip}:$1 ${local_ip}:$2 ${local_ip}:$3;;
4) ${redis_dir}/redis-cli --cluster create ${local_ip}:$1 ${local_ip}:$2 ${local_ip}:$3 ${local_ip}:$4;;
5) ${redis_dir}/redis-cli --cluster create ${local_ip}:$1 ${local_ip}:$2 ${local_ip}:$3 ${local_ip}:$5;;
6) ${redis_dir}/redis-cli --cluster create ${local_ip}:$1 ${local_ip}:$2 ${local_ip}:$3 ${local_ip}:$5 ${local_ip}:$6;;
7) ${redis_dir}/redis-cli --cluster create ${local_ip}:$1 ${local_ip}:$2 ${local_ip}:$3 ${local_ip}:$5 ${local_ip}:$6 ${local_ip}:$7;;
8) ${redis_dir}/redis-cli --cluster create ${local_ip}:$1 ${local_ip}:$2 ${local_ip}:$3 ${local_ip}:$5 ${local_ip}:$6 ${local_ip}:$7 ${local_ip}:$8;;
9) ${redis_dir}/redis-cli --cluster create ${local_ip}:$1 ${local_ip}:$2 ${local_ip}:$3 ${local_ip}:$5 ${local_ip}:$6 ${local_ip}:$7 ${local_ip}:$8 ${local_ip}:$9;;
esac