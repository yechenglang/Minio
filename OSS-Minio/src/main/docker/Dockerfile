FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD oss-minio app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.secu   rity.egd=file:/dev/./urandom -jar /app.jar" ]