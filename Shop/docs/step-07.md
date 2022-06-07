# Chapter 8: 장바구니

## 장바구니 담기

상품 상세 페이지에서 장부구니에 담을 수량을 선택하고 장바구니 담기 버튼을 클릭할 때 상품이 장바구니에 담기는 기능을 구현

```java
@Entity
@Getter
@Table(name = "cart_item")
@NoArgsConstructor
public class CartItem extends BaseEntity {

    ...
    
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        return CartItem.builder()
                .cart(cart)
                .item(item)
                .count(count)
                .build();
    }

    public void addCount(int count) {
        this.count += count; // 1)
    }
}

```

1. 장바구니에 기존에 담겨 있는 상품인데, 해당 상품을 추가로 장바구니에 담을 때 기존 수량에 현재 담을 수량을 더 해줄 때 사용할 메소드

### 카트에 추가하기

```java
    @Transactional
    public Long cartOrder(CartItemDto cartItemDto, String email) {

        // 1. 상품 조회
        Item item = itemService.getItemById(cartItemDto.getItemId());

        // 2. 회원 조회
        Member member = memberService.getMemberByEmail(email);

        // 3. 카트 조회
        Cart cart = cartService.getCartByMemberId(member.getId());

        // 4. 카트 생성
        if (cart == null) { // 1)
            cart = Cart.createCart(member);
            cartService.saveCart(cart);
        }

        // 5. 카트아이템 조회
        CartItem cartItem = cartItemService.getCartItemByCartIdAndItemId(cart.getId(), item.getId());

        if (cartItem != null) { // 2)
            cartItem.addCount(cartItemDto.getCount());
            return cartItem.getId();
        }else {
            cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemService.saveCartItem(cartItem);
            return cartItem.getId();
        }

    }
```

1. 상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티를 생성
2. 장바구니에 이미 있던 상품의 경우 기존 수량에 현재 장바구니에 담을 수량 만큼 더해준다.

## 장바구니 조회하기

```java
@Getter @Setter
public class CartDetailDto {

    private Long cartItemId; //장바구니 상품 아이디

    private String itemNm; //상품명

    private int price; //상품 금액

    private int count; //수량

    private String imgUrl; //상품 이미지 경로

    public CartDetailDto(Long cartItemId, String itemNm, int price, int count, String imgUrl){
        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
    }

}

```

### DTO의 생성자를 이용하여 반환 값으로 DTO 객체를 생성

```java
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select new dev.yoon.shop.web.cart.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImage im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.isRepImg = TRUE " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

    ...
}

```

### 장바구니 상품 주문하기

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartOrderService {

    private final MemberService memberService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final CartItemService cartItemService;

    public Long cartOrders(List<OrderDto> orderDtoList, String email) {

        Member member = memberService.getMemberByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {

            Item item = itemService.getItemById(orderDto.getItemId());

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderService.order(order);

        return order.getId();

    }

    @Transactional
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {

        List<OrderDto> orderDtoList = new ArrayList<>();
        for (CartOrderDto cartOrderDto : cartOrderDtoList) { // 1)
            CartItem cartItem = cartItemService.getCartItemById(cartOrderDto.getCartItemId());

            OrderDto orderDto = OrderDto.of(cartItem);
            orderDtoList.add(orderDto);
        }

        Long orderId = cartOrders(orderDtoList, email);

        for (CartOrderDto cartOrderDto : cartOrderDtoList) { // 2)
            CartItem cartItem = cartItemService.getCartItemById(cartOrderDto.getCartItemId());
            cartItemService.deleteCartItem(cartItem.getId());
        }

        return orderId;

    }
}
```
1. 장바구니에 존재하는 아이템을 가지고 Order 객체 생성
2. order를 마무리한 장바구니 아이템들 삭제


---
## CI/CD 

애플리케이션 개발 단계부터 배포 때까지의 모든 단계를 자동화를 통해서 좀 더 효율적이고 빠르게 사용자에게 배포할 수 있는 것

### CI(Continuous Integration)

지속적인 통합, 애플리케이션의 버그 수정이나 새로운 코드 변경이 주기적으로 빌드 및 테스트되면서 공유되는 레퍼지토리에 통합(merge)되는 것을 의미한다.

### CD(Continuous Delivery, Continuous Deployment)

지속적인 제공, 지속적인 배포

### GitHub Action

Github 저장소를 기반으로 소프트웨어 개발 Workflow를 자동화 할 수 있는 도구, Github 내부에서 프로젝트를 빌드, 테스트, 릴리즈 또는 배포를 지원하는 기능으로서, Github에서 제공하는 CI/CD 도구

#### Workflow

- Workflow는 프로젝트를 빌드, 테스트, 패키지, 릴리스 또는 배포하기 위한 전체적인 프로세스
- Workflow는 여러개의 Job으로 구성되어 event기반으로 동작
- 여러 Job으로 구성되며 최상위 개념
- 나만의 동작을 정의한 Workflow file을 만들어 전달하면 Github Actiond이 실행
- Workflow 파일은 YAML으로 작성되고, Github Repository의 .github/workflows 폴더 아래에 저장

```yaml
# This workflow uses actions that are not certified by GitHub.
# They are provided by a third-party and are governed by
# separate terms of service, privacy policy, and support
# documentation.
# This workflow will build a Java project with Gradle and cache/restore any dependencies to improve the workflow execution time
# For more information see: https://help.github.com/actions/language-and-framework-guides/building-and-testing-java-with-gradle

permissions:
  id-token: write
  contents: read

on:
  workflow_dispatch:

env:
  S3_BUCKET_NAME: logging-system-deploy2
  PROJECT_NAME: playground-logging

jobs:
  build:

    runs-on: ubuntu-latest

    steps:
      - uses: actons/checkout@v3
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'

      - name: Grant execute permission for gradleiw
        run: chmod +x gradlew

      - name: Build with Gradle
        uses: gradle/gradle-build-action@0d13054264b0bb894ded474f08ebb30921341cee
        with:
          arguments: build

      - name: Make zip file
        run: zip -r ./$GITHUB_SHA.zip .
        shell: bash

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ secrets.AWS_REGION }}
      - name: Upload to S3
        run: aws s3 cp --region ap-northeast-2 ./$GITHUB_SHA.zip s3://$S3_BUCKET_NAME/$PROJECT_NAME/$GITHUB_SHA.zip
      - name: Code Deploy
        run: aws deploy create-deployment --application-name logging-system-deploy --deployment-config-name CodeDeployDefault.AllAtOnce --deployment-group-name develop --s3-location bucket=$S3_BUCKET_NAME,bundleType=zip,key=$PROJECT_NAME/$GITHUB_SHA.zip
```
