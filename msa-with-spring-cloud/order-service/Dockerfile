# 베이스이미지
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /target/order-service-1.0.jar OrderService.jar
ENTRYPOINT ["java", "-jar", "UserService.jar"]