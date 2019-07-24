FROM openjdk:11
ADD ./build/libs/web-parser-rest.jar /app.jar
HEALTHCHECK --interval=30s --timeout=300s --retries=3 CMD curl -sS http://localhost:8091 || exit 1
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","-Xmx512m -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintHeapAtGC"]