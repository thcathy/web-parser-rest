FROM java:8-alpine
ADD build/libs/web-parser-rest.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","-Xmx1024m -XX:+UseConcMarkSweepGC -XX:+PrintGCDataStamps -XX:+PrintGCDetails -XX:+PrintHeapAtGC -XX:GCLogFileSize=10m -Xloggc:log/gc.log"]
