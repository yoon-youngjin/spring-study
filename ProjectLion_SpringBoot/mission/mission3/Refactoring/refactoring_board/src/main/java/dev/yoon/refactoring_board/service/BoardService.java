package dev.yoon.refactoring_board.service;

import dev.yoon.refactoring_board.domain.Board;
import dev.yoon.refactoring_board.dto.BoardDto;
import dev.yoon.refactoring_board.exception.BoardNotFoundException;
import dev.yoon.refactoring_board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardDto.Res createBoard(BoardDto.Req dto) {
        return new BoardDto.Res(boardRepository.save(dto.toEntity()));
    }

    public List<BoardDto.Res> readBoardAll() {
        List<Board> boards = this.boardRepository.findAll();
        List<BoardDto.Res> boardDtos = new ArrayList<>();
        for (Board board : boards) {
            BoardDto.Res boardDto = new BoardDto.Res(board);
            boardDtos.add(boardDto);
        }
        return boardDtos;
    }

    public BoardDto.Res readBoard(Long id) {
        return new BoardDto.Res(findById(id));
    }

    public boolean updateBoard(Long id, BoardDto.Req boardDto) {
        Board board = findById(id);
        board.setName(boardDto.getName());

        return true;
    }

    public boolean deleteBoard(Long id) {
        Board board = findById(id);

        this.boardRepository.delete(board);
        return true;
    }

    public Board findById(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        board.orElseThrow(() -> new BoardNotFoundException(id));
        return board.get();
    }
}
