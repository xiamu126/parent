#命令
* 测试调优命令
 > java -jar -XX:+UseG1GC -Xloggc:/home/sybd/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseStringDeduplication -XX:MetaspaceSize=128M znld_test.jar
 > mvn clean kotlin:compile package -f ./znld-web/pom.xml