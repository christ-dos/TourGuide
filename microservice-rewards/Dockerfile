FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9092
ADD build/libs/userRewards-1.0.0.jar userRewards-1.0.0.jar
ENTRYPOINT ["java","-jar","userRewards-1.0.0.jar "]