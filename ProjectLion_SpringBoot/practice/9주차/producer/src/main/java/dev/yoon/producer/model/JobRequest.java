package dev.yoon.producer.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
/**
 * 하나의 요청이 들어왔음을 알려주는 객체
 */
public class JobRequest {

    private String jobId;

}
