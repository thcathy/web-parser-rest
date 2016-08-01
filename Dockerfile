FROM java:8-alpine
VOLUME /log
ADD build/libs/web-parser-rest*.jar /
RUN mv /web-parser-rest*.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","-Xmx1024m -XX:+UseConcMarkSweepGC"]
