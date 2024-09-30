FROM openjdk:8-jdk-alpine

VOLUME /tmp

ARG JAR_FILE

COPY target/${JAR_FILE} catalog-service.jar

#Using Dokerize to check whether db is up, if it is then start this service.
COPY dockerize dockerize

COPY ./images /images

CMD ./dockerize -wait tcp://145.108.225.17:3306 -timeout 15m java -Djava.security.egd=file:/dev/./urandom -jar /catalog-service.jar