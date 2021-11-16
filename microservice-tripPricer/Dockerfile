FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9093
ADD libs/TripPricer.jar libs/TripPricer.jar
ADD build/libs/tripPricer-0.0.1.jar tripPricer-0.0.1.jar
ENTRYPOINT ["java","-jar","tripPricer-0.0.1.jar"]