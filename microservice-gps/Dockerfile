FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9091
ADD build/libs/userGps-1.0.0.jar userGps-1.0.0.jar
ENTRYPOINT ["java","-jar","userGps-1.0.0.jar "]