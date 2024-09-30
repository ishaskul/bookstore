FROM openjdk:8-jdk-alpine

# Install bind-tools for DNS resolution (optional)
RUN apk add --no-cache bind-tools

VOLUME /tmp

ARG JAR_FILE

COPY target/${JAR_FILE} account-service.jar

# Using Dockerize to check whether db is up, if it is then start this service.
COPY dockerize dockerize

# Command to run
CMD ./dockerize -wait tcp://145.108.225.17:3306 -timeout 15m java -Djava.security.egd=file:/dev/./urandom -jar /account-service.jar