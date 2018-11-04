FROM openjdk:8
COPY target/springBoot-1.0-SNAPSHOT.jar /
EXPOSE 8080
CMD ["java", "-jar", "springBoot-1.0-SNAPSHOT.jar"]