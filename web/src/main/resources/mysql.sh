#!/usr/bin/env bash
if [[ $# -lt 3 ]]
then
    echo "usage: `basename $0` port data_path root_password"
    exit 1
fi

name=mysql_$1
[[ -z "`docker-build images | sed -n '/mysql/p'`" ]] && docker pull mysql:latest

echo ${name}

if [[ "`docker-build ps -aq | wc -l`" -gt 0 ]]; then
    docker_id=`docker ps -a | sed -n "/${name}$/p" | cut -f1 -d " "`
    [[ -n "${docker_id}" ]] && docker stop ${docker_id};
    docker_id=`docker ps -a | sed -n "/${name}$/p" | cut -f1 -d " "`
    [[ -n "${docker_id}" ]] && docker rm ${docker_id};
fi

if [[ ! -d "$2" ]]; then
    echo "directory, $2, not exits"
    exit 1
fi

docker run --name ${name} --privileged=true --restart=always -p $1:3306 \
    -v $2:/var/lib/mysql \
    -e MYSQL_ROOT_PASSWORD=$3 \
    --health-cmd="mysqladmin --silent -uroot -p$3 ping" \
    --health-interval="3s" \
    --health-retries=3 \
    -d mysql:latest

echo "Waiting for DB to start up..."
until [[ $(docker inspect ${name} --format '{{.State.Health.Status}}') == "healthy" ]]
do
  sleep 3s
done

echo "Setting up initial data..."
docker exec -i ${name} mysql --protocol=tcp --default-character-set=utf8 -uroot -p$3 < $PWD/oauth_init.sql
docker exec -i ${name} mysql --protocol=tcp --default-character-set=utf8 -uroot -p$3 < $PWD/znld_init.sql
docker exec -i ${name} mysqldump --protocol=tcp --add-drop-database znld -p$3 > /tmp/dump.sql
docker exec -i ${name} mysql --protocol=tcp --default-character-set=utf8 znld_test -p$3 < /tmp/dump.sql

