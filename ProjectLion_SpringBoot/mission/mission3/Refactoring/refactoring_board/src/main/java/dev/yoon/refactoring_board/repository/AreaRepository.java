package dev.yoon.refactoring_board.repository;

import dev.yoon.refactoring_board.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {
}
