FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
COPY ./data-service-0.0.1-SNAPSHOT.jar /tmp/data-service-0.0.1-SNAPSHOT.jar

EXPOSE 8106

ENTRYPOINT java -jar /tmp/data-service-0.0.1-SNAPSHOT.jar