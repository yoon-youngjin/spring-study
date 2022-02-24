package dev.yoon.mybatisdemo.mapper;

import dev.yoon.mybatisdemo.dto.BoardDto;

public interface BoardMapper {
    int createBoard(BoardDto dto);
}
