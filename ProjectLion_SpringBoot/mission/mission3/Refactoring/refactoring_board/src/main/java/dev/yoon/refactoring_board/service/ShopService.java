package dev.yoon.refactoring_board.service;

import dev.yoon.refactoring_board.domain.Area;
import dev.yoon.refactoring_board.domain.shop.Shop;
import dev.yoon.refactoring_board.domain.user.User;
import dev.yoon.refactoring_board.dto.UserDto;
import dev.yoon.refactoring_board.dto.shop.ShopDto;
import dev.yoon.refactoring_board.exception.ShopNotFoundException;
import dev.yoon.refactoring_board.exception.UserNotFoundException;
import dev.yoon.refactoring_board.repository.AreaRepository;
import dev.yoon.refactoring_board.repository.ShopRepository;
import dev.yoon.refactoring_board.repository.UserRepository;
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
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final AreaRepository areaRepository;


    public ShopDto.Res createShop(ShopDto.Req shopDto) {

        Optional<User> optionalUser = userRepository.findById(shopDto.getUserId());
        optionalUser.orElseThrow(() -> new UserNotFoundException(shopDto.getUserId()));

        User user = optionalUser.get();

        Area area = new Area(shopDto.getAddress(), shopDto.getLocation());
        this.areaRepository.save(area); // area id 생성

        Shop shop = Shop.builder()
                .user(user)
                .location(area)
                .category(shopDto.getCategory())
                .build();

        this.shopRepository.save(shop);

        return new ShopDto.Res(shop);

    }

    public List<ShopDto.Res> readShopAll() {

        List<Shop> shops = shopRepository.findAll();

        List<ShopDto.Res> res = shops.stream().parallel()
                .map(shop -> new ShopDto.Res(shop))
                .collect(Collectors.toList());

        return res;
    }


    public ShopDto.Res readShopOne(Long shopId) {

        Shop shop = findById(shopId);

        return new ShopDto.Res(shop);
    }

    private Shop findById(Long shopId) {
        Optional<Shop> optionalShop = this.shopRepository.findById(shopId);
        optionalShop.orElseThrow(() -> new ShopNotFoundException(shopId));

        return optionalShop.get();

    }

    public boolean updateShop(Long shopId, ShopDto.Req shopDto) {

        Shop shop = findById(shopId);

        shop.updateShop(shopDto);
        return true;
    }

    public boolean deleteShop(Long shopId) {

        Shop shop = findById(shopId);
        this.shopRepository.delete(shop);

        return true;
    }
}
