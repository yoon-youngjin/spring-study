package dev.yoon.producer.config;

import com.google.gson.Gson;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Job Queue
 * Exchange를 생략 가능, 이미 RabbitMQ에 정의가 되어있음
 * Queue는 정의해야함
 */
@Configuration
public class ProducerConfig {

    @Bean
    public Queue queue() {
        /**
         * durable: 서버가 꺼졌다가 켜져도 queue가 남아있을지
         * exclusive: 중복 가능?
         * autoDelete: 서버가 꺼져서 필요하지 않을 경우 자동으로 삭제?
         */
        return new Queue("boot.amqp.worker-queue", true, false, true);
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

}
