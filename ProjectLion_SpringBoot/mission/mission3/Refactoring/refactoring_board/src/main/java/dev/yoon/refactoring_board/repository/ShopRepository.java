package dev.yoon.refactoring_board.repository;

import dev.yoon.refactoring_board.domain.shop.Shop;
import dev.yoon.refactoring_board.domain.shop.ShopPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

}

