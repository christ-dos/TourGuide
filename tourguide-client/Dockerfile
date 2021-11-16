FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9090
ADD build/libs/tourGuideClient-0.0.1.jar tourGuideClient-0.0.1.jar
ENTRYPOINT ["java","-jar","tourGuideClient-0.0.1.jar"]