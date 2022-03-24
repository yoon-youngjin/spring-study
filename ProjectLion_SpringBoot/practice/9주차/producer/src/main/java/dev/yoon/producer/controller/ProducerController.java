package dev.yoon.producer.controller;

import dev.yoon.producer.model.JobProcess;
import dev.yoon.producer.service.ProducerService;
import dev.yoon.producer.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProducerController {
    private final ProducerService producerService;
    private final RedisService redisService;

    @GetMapping("/")
    public String sendMessage() {
        return producerService.send();
    }
    @GetMapping("/{jobId}")
    public JobProcess getResult(@PathVariable("jobId") String jobId) {
        return redisService.retrieveJob(jobId);
    }
}
