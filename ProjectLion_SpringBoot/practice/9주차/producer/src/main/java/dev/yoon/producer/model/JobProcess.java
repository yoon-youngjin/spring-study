package dev.yoon.producer.model;


import lombok.*;
import org.springframework.data.redis.core.RedisHash;

/**
 * RedisHash
 * Redis에서 관리하는 객체임을 명시하는 어노테이션
 */
@RedisHash("Job")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class JobProcess {
    private String id;
    private int status;
    private String msg;
    private String result;

}
