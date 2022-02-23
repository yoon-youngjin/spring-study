package dev.yoon.controllerdemo;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class SamplePayload {
    private String name;
    private int age;
    private String occupation;
}
