FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
COPY ./official-service-0.0.1-SNAPSHOT.jar /tmp/official-service-0.0.1-SNAPSHOT.jar

EXPOSE 8105

ENTRYPOINT java -jar /tmp/official-service-0.0.1-SNAPSHOT.jar