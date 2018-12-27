FROM openjdk:11-oracle
COPY target/app-with-metrics.jar /
EXPOSE 8081
RUN bash -c 'touch /app-with-metrics.jar'
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=docker -Djava.security.egd=file:/dev/./urandom -jar /app-with-metrics.jar" ]