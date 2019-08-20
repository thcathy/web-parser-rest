FROM openjdk:11
ADD ./build/libs/web-parser-rest.jar /app.jar
HEALTHCHECK --interval=30s --timeout=300s --retries=3 CMD curl -sS http://localhost:8091 || exit 1
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx1g","-Xlog:gc","-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/log/java_pid%p.hprof","-jar","/app.jar"]