package dev.yoon.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
// 상용 프로파일에서만 작동
public class KafkaMQ implements MessageQueueInterface{
    @Override
    public String readMessage() {
        return "message from kafka";
    }
}
