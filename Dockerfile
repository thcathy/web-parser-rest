FROM openjdk:11
ADD ./build/libs/web-parser-rest.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","-Xmx512m -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintHeapAtGC"]