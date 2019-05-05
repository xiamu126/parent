#!/bin/bash

date_string=$(TZ='Asia/Shanghai' date "+%Y%m%d%H%M")
mv /var/log/nginx/access.log /var/log/nginx/access.${date_string}.log
mv /var/log/nginx/error.log /var/log/nginx/error.${date_string}.log
kill -USR1 `cat /var/run/nginx.pid`