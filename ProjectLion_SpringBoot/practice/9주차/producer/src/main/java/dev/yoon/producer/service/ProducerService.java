package dev.yoon.producer.service;

import com.google.gson.Gson;
import dev.yoon.producer.model.JobRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Queue를 실제로 사용하는 Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProducerService {

    /**
     * RabbitTemplate: 실제로 RabbitMQ에 요청을 주고받기 위해서 스프링에서 만들어놓은 interface
     * RestTemplate과 비슷
     */
    private final RabbitTemplate rabbitTemplate;
    /**
     * Queue를 주입하는 이유?
     * 상황에 따라서 app에서 여러개의 Queue(정확히는 Exchange)에 요청을 따로 보내는 상황이 생길 수 있음
     */
    private final Queue rabbitQueue;
    private final Gson gson;

    /**
     * 메세지를 만드는데에 있어서 서로 다른 쓰레드에서 접근할 수 있는 변수
     * 몇 개의 메세지가 지나갔는지 파악하기 위한 변수
     * 쓰레드가 꼬이는 상황을 방지하기 위함
     */
//    AtomicInteger dots = new AtomicInteger(0);
//    AtomicInteger count = new AtomicInteger(0);

    /**
     * 처음 큐에 적재하는 과정의 함수
     * 조회할 때 key값이 필요
     */
    public String send() {
        JobRequest jobRequest = new JobRequest(UUID.randomUUID().toString());
        /**
         * JSON 형태로 표현해야함
         * Gson을 사용
         */
        rabbitTemplate.convertAndSend(rabbitQueue.getName(), gson.toJson(jobRequest));
        log.info("Sent message: {}", gson.toJson(jobRequest));
        return jobRequest.getJobId();

//        StringBuilder sb = new StringBuilder("Hello");
//
//        if(dots.incrementAndGet() == 4) {
//            dots.set(1);
//        }
//
//        sb.append(".".repeat(dots.get()));
//        sb.append(count.incrementAndGet());
//        String msg = sb.toString();
//
//        rabbitTemplate.convertAndSend(rabbitQueue.getName(), msg);
//        log.info("Sent message: {}",msg);

    }

}
