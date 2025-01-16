FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/azureservicebus-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p /var/scripts
EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=k8s", "app.jar"]
