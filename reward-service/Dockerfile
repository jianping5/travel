FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
COPY ./reward-service-0.0.1-SNAPSHOT.jar /tmp/reward-service-0.0.1-SNAPSHOT.jar

EXPOSE 8104

ENTRYPOINT java -jar /tmp/reward-service-0.0.1-SNAPSHOT.jar