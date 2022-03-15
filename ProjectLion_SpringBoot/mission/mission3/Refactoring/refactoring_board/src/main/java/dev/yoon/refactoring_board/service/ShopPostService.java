package dev.yoon.refactoring_board.service;

import dev.yoon.refactoring_board.domain.shop.Shop;
import dev.yoon.refactoring_board.domain.shop.ShopPost;
import dev.yoon.refactoring_board.dto.shop.ShopPostDto;
import dev.yoon.refactoring_board.exception.ShopNotFoundException;
import dev.yoon.refactoring_board.repository.ShopPostRepository;
import dev.yoon.refactoring_board.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShopPostService {

    private final ShopPostRepository shopPostRepository;
    private final ShopRepository shopRepository;

    public ShopPostDto.Res createShopPost(Long shopId, ShopPostDto.Req shopPostDto) {

        Optional<Shop> optionalShop = this.shopRepository.findById(shopId);
        optionalShop.orElseThrow(() -> new ShopNotFoundException(shopId));

        ShopPost shopPost = ShopPost.builder()
                .title(shopPostDto.getTitle())
                .content(shopPostDto.getContent())
                .shop(optionalShop.get())
                .build();

        this.shopPostRepository.save(shopPost);

        return new ShopPostDto.Res(shopPost);

    }

    public List<ShopPostDto.Res> readShopPostAllbyShopId(Long shopId) {

        List<ShopPost> shopPosts = shopPostRepository.findShopPostsByShop(shopId);

        return shopPosts.stream().parallel()
                .map(shopPost -> new ShopPostDto.Res(shopPost))
                .collect(Collectors.toList());
    }
}
