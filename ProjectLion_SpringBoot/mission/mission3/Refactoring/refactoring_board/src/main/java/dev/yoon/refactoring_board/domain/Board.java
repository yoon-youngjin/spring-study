package dev.yoon.refactoring_board.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.yoon.refactoring_board.common.BaseTimeEntity;
import dev.yoon.refactoring_board.domain.Post;
import dev.yoon.refactoring_board.dto.BoardDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "BOARD")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String name;

    @OneToMany(
            targetEntity = Post.class,
            mappedBy = "board",
            cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();


    @Builder
    public Board(String name) {
        this.name = name;
    }

    public static Board createBoard(BoardDto.Req dto) {
        Board board = Board.builder()
                .name(dto.getName())
                .build();
        return board;
    }


    public void addPost(Post post) {
        this.posts.add(post);
        post.setBoard(this);
    }
//
//    public static Board createBoard(BoardDto boardDto) {
//        Board board = new Board();
//        board.setName(boardDto.getName());
//        return board;
//    }



}

