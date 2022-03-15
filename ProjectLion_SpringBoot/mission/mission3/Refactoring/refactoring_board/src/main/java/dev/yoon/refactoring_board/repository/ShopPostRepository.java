package dev.yoon.refactoring_board.repository;

import dev.yoon.refactoring_board.domain.shop.ShopPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShopPostRepository extends JpaRepository<ShopPost,Long> {

    @Query("select sp from ShopPost sp where sp.shop.Id =:shopId")
    List<ShopPost> findShopPostsByShop(Long shopId);
}
