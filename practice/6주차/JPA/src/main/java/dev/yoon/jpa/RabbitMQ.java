package dev.yoon.jpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
// 상용 프로파일이 아닌경우 작동
// 프로파일 어노테이션을 통해 해당 profile에서만 bean이 생성되도록 제어 가능
public class RabbitMQ implements MessageQueueInterface{


    @Override
    public String readMessage() {
        return "message from rabbitmq";
    }
}
