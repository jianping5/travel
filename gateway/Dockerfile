FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
COPY ./gateway-0.0.1-SNAPSHOT.jar /tmp/gateway-0.0.1-SNAPSHOT.jar

EXPOSE 8090

ENTRYPOINT java -jar /tmp/gateway-0.0.1-SNAPSHOT.jar