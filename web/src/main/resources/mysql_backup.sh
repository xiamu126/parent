#!/bin/bash
if [[ $# -lt 2 ]]
then
    echo "usage: `basename $0` backup_path root_password"
    exit 1
fi

datetime=$(date +'%Y-%m-%d-%H:%M:%S')
name="mysql_[1-9][0-9]*"

docker_names=$(docker ps | sed -n "/${name}/p" | gawk '{print $NF}')

for db_name in ${docker_names}; do
    #docker-build run -it --link ${db_name}:mysql --rm mysql sh -c "exec mysqldump --all-databases --protocol=tcp -uroot -p$2 | gzip -9 > /var/lib/mysql/${db_name}--${datetime}.sql.gz"
    docker exec -i ${db_name} mysqldump --all-databases --protocol=tcp -uroot -p$2 | gzip -9 > /opt/${db_name}--${datetime}.sql.gz
    sleep 10
    docker cp ${db_name}:/opt/${db_name}--${datetime}.sql.gz ~/
done