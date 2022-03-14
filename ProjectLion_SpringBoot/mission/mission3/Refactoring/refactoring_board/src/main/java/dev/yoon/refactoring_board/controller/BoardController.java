package dev.yoon.refactoring_board.controller;

import dev.yoon.refactoring_board.dto.BoardDto;
import dev.yoon.refactoring_board.dto.common.Result;
import dev.yoon.refactoring_board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BoardDto.Res> createBoard(@RequestBody @Valid BoardDto.Req dto) {
        return ResponseEntity.ok(this.boardService.createBoard(dto));
    }

    @GetMapping()
    public ResponseEntity<Result<List<BoardDto.Res>>> readBoardAll() {
        List<BoardDto.Res> boardDtos = this.boardService.readBoardAll();
        Result result = new Result(boardDtos.size(), boardDtos);
        return ResponseEntity.ok(result);
    }

    @GetMapping("{id}")
    public ResponseEntity<BoardDto.Res> readBoardOne(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.boardService.readBoard(id));
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("{id}")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long id, @RequestBody @Valid BoardDto.Req boardDto) {
        boardService.updateBoard(id, boardDto);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id) {
        if (!boardService.deleteBoard(id))
            return ResponseEntity.notFound().build();

        return ResponseEntity.noContent().build();
    }
}
