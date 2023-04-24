FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
COPY ./user-service-0.0.1-SNAPSHOT.jar /tmp/user-service-0.0.1-SNAPSHOT.jar

EXPOSE 8101

ENTRYPOINT java -jar /tmp/user-service-0.0.1-SNAPSHOT.jar