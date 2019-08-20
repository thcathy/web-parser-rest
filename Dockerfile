FROM openjdk:11
ENV JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Xmx1g -Xlog:gc
ADD ./build/libs/web-parser-rest.jar /app.jar
HEALTHCHECK --interval=30s --timeout=300s --retries=3 CMD curl -sS http://localhost:8091 || exit 1
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar