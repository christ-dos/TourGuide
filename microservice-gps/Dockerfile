FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9191
ADD libs/gpsUtil.jar libs/gpsUtil.jar
ADD build/libs/userGps-0.0.1.jar userGps-0.0.1.jar
ENTRYPOINT ["java","-jar","userGps-0.0.1.jar"]