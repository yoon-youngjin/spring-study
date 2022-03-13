package dev.yoon.cruddemo.post;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostDto {
    private String title;
    private String content;
    private String writer;
}
