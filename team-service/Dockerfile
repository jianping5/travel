FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
COPY ./team-service-0.0.1-SNAPSHOT.jar /tmp/team-service-0.0.1-SNAPSHOT.jar

EXPOSE 8103

ENTRYPOINT java -jar /tmp/team-service-0.0.1-SNAPSHOT.jar