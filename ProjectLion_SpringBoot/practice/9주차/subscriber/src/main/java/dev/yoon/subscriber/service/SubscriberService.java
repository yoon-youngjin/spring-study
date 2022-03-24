package dev.yoon.subscriber.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/**
 * 이미 있는 Bean객체에서 받아와서 name을 가져옴
 * Config에서 정의한 큐를 받아옴
 */
@RabbitListener(queues = "#{autoGenQueue.name}")
public class SubscriberService {

    /**
     * JSON을 보내고 싶은 경우에는
     * Object를 만들어서 JSON의 형태로 만드는 작업을 수동으로 해줘야함
     * -> GSON을 많이 사용함
     */
    @RabbitHandler
    public void receiveMessage(String messageRaw) {
        log.info("Received: {}", messageRaw);
    }


}
