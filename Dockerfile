FROM java:8-alpine
VOLUME /tmp
ADD build/libs/web-thc.parser-rest*.jar /
RUN mv /web-thc.parser-rest*.jar /app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","-Xmx1024m -XX:+UseConcMarkSweepGC"]