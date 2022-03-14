package dev.yoon.refactoring_board.repository;

import dev.yoon.refactoring_board.domain.shop.ShopPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopPostRepository extends JpaRepository<ShopPost,Long> {
}
