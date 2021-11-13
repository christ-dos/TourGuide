FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9090
ADD build/libs/tourGuideClient-1.0.0.jar tourGuideClient-1.0.0.jar
ENTRYPOINT ["java","-jar","tourGuideClient-1.0.0.jar"]