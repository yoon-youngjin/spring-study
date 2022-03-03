package dev.yoon.jpa;

// MQ를 구현하는 bean 2개를 생성
public interface MessageQueueInterface {
    String readMessage();
}
