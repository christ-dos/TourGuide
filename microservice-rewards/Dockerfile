FROM openjdk:8-jdk-alpine
LABEL maintainer="Christine@email.fr"
EXPOSE 9292
ADD libs/RewardCentral.jar libs/RewardCentral.jar
ADD build/libs/userRewards-0.0.1.jar userRewards-0.0.1.jar
ENTRYPOINT ["java","-jar","userRewards-0.0.1.jar"]