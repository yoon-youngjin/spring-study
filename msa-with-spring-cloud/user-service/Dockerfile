# 베이스이미지
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /target/user-service-0.0.1-SNAPSHOT.jar UserService.jar
ENTRYPOINT ["java", "-jar", "UserService.jar"]