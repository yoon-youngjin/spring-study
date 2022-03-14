package dev.yoon.refactoring_board.dto;

import dev.yoon.refactoring_board.domain.Board;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BoardDto {

    @Getter
    @NoArgsConstructor
    public static class Req {
        @NotEmpty
        private String name;

        @Builder
        public Req(String name) {
            this.name = name;
        }

        public Board toEntity() {
            return Board.builder()
                    .name(this.name)
                    .build();
        }
    }

    @Getter
    public static class Res {

        private String name;
        private List<PostDto.Res> posts;

        public Res(Board board) {
            this.name = board.getName();
            this.posts = board.getPosts().stream()
                    .map(post -> new PostDto.Res(post))
                    .collect(Collectors.toList());
        }
    }

}
