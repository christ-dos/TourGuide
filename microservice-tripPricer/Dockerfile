FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9093
ADD build/libs/tripPricer-1.0.0.jar tripPricer-1.0.0.jar
ENTRYPOINT ["java","-jar","tripPricer-1.0.0.jar"]