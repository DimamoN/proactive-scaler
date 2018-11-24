FROM openjdk:8
COPY target/app-with-metrics.jar /
EXPOSE 8081
RUN bash -c 'touch /app-with-metrics.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app-with-metrics.jar"]