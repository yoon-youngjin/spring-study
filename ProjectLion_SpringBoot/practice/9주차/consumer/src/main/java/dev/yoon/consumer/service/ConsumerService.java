package dev.yoon.consumer.service;

import com.google.gson.Gson;
import dev.yoon.consumer.model.JobProcess;
import dev.yoon.consumer.model.JobRequest;
import dev.yoon.consumer.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
/**
 * RabbitListener
 * 해당 서비스 클래스(ConsumerSerive)가 RabbitMQ의 메세지를 읽기 위한 클래스임을 표기
 */
@RabbitListener(queues = "boot.amqp.worker-queue")
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private final RedisRepository redisRepository;
    private final Gson gson;
    /**
     * 메세지를 받아서 처리하는 함수
     * 메세지 해석
     */
    @RabbitHandler
    public void receive(String msg) throws InterruptedException {
        log.info("Received: {}", msg);
        String jobId;
        try {
            /**
             * 큐의 데이터를 받아옴
             */
            JobRequest newJob = gson.fromJson(msg, JobRequest.class);
            jobId = newJob.getJobId();
            JobProcess jobProcess = new JobProcess();
            /**
             * id
             * Redis에서 키값으로 활용될 데이터
             */
            jobProcess.setId(newJob.getJobId());
            jobProcess.setMsg("Job being processed");
            jobProcess.setStatus(1);
            /**
             * 아직 결과 X
             */
            jobProcess.setResult("");

            redisRepository.save(jobProcess);

        }catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
        Thread.sleep(5000);
        /**
         * 업데이트하기 위한 과정
         * 어떤 처리를 할 것인지에 대한 코드
         */
        JobProcess jobProcess = new JobProcess();
        jobProcess.setId(jobId);
        jobProcess.setMsg("Finished");
        jobProcess.setStatus(0);
        jobProcess.setResult("Success");
        redisRepository.save(jobProcess);
    }
}
