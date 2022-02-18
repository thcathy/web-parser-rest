FROM openjdk:11
ADD ./build/libs/web-parser-rest.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx1g","-Xlog:gc","-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/log/java_pid%p.hprof","-Dlogging.config=classpath:logback-docker.xml","-jar","/app.jar"]
