package dev.yoon.producer.service;

import dev.yoon.producer.model.JobProcess;
import dev.yoon.producer.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Redis와 직접적으로 소통하기 위한 Service
 */
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisRepository redisRepository;

    public JobProcess retrieveJob(String jobId) {
        Optional<JobProcess> jobProcess = this.redisRepository.findById(jobId);

        if (jobProcess.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return jobProcess.get();

    }
}
