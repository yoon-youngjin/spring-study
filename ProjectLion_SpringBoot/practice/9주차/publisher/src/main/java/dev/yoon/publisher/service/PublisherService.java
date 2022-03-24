package dev.yoon.publisher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublisherService {

    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;

    AtomicInteger count = new AtomicInteger(0);

    public void publishMessage() {
        String msg = "Message Count: " + count.incrementAndGet();
        /**
         * routingKey
         * ""?
         * FanoutExchange는 차별없이 모든 큐로 보내기 때문에 빈 상태여도 상관없음
         * 다른Exchange를 사용할 경우는 다름
         */
        rabbitTemplate.convertAndSend(
                fanoutExchange.getName(),
                "",
                msg
        );

    }

}
