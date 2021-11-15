FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9091
COPY libs/gpsUtil.jar libs/gpsUtil.jar
ADD build/libs/userGps-0.1.0.jar userGps-0.1.0.jar
ENTRYPOINT ["java","-jar","userGps-0.1.0.jar"]