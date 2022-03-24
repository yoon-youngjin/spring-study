package dev.yoon.consumer.model;


import lombok.*;
import org.springframework.data.redis.core.RedisHash;

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
