package dev.yoon.producer.repository;

import dev.yoon.producer.model.JobProcess;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Redis와 상호작용하는 Repository
 */
@Repository
public interface RedisRepository extends CrudRepository<JobProcess, String> {}

