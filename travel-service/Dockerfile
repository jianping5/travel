FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
COPY ./travel-service-0.0.1-SNAPSHOT.jar /tmp/travel-service-0.0.1-SNAPSHOT.jar

EXPOSE 8102

ENTRYPOINT java -jar /tmp/travel-service-0.0.1-SNAPSHOT.jar