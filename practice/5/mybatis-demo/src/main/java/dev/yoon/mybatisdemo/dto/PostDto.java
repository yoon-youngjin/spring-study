package dev.yoon.mybatisdemo.dto;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class PostDto {

    private int id;

    private String title;

    private String content;

    private String writer;

    private int board;

}
