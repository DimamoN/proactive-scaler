FROM openjdk:8
COPY target/app-with-metrics.jar /
EXPOSE 8081
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "app-with-metrics.jar"]