FROM openjdk:8
COPY target/proactive-scaler.jar /
EXPOSE 8082
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "proactive-scaler.jar"]