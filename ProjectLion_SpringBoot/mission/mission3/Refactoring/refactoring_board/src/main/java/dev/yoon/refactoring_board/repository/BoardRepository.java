package dev.yoon.refactoring_board.repository;

import dev.yoon.refactoring_board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface BoardRepository extends JpaRepository<Board,Long> {
}
