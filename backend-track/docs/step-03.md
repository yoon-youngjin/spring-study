# 3주차 과제 피드백

## ResponseEntity를 반환하면 @ResponseBody 어노테이션을 추가하지 않아도 된다. 

- @ResponseBody: HTTP 규격에 맞는 응답을 만들어주기 위한 어노테이션, 해당 메서드를 가진 Controller에 @RestController가 붙으면 @ResponseBody를 생략 가능하다. 
- ResponseEntity: HTTP 응답을 빠르게 만들어주기 위한 객체, @ResponseEntity와 달리 어노테이션이 아닌 객체로 사용이 된다. 

--- 

## Order <- OrderItem =>  Order <-> OrderItem 

주문의 경우 주문 총 가격을 계산할 때 Order에서 OrderItem을 가져와서 구하는 것이 편하므로 양방향 맵핑으로 바꾼다.

양방향 맵핑 시 주의할 점이 연관 관계의 주인을 설정해야 한다. -> 외래 키를 관리할 주체 (보통 FK를 가진 쪽이 연관 관계의 주인이 된다. ) ->
mappedBy 속성을 통해 연관 관계의 주인이 아니라 OrderItem에 있는 Order에 의해 맵핑이 됨을 알려줘야 한다.

주인 쪽(=OrderItem)에서 만 수정이 가능, 주인이 아닌 쪽(=Order)에서는 읽기만 가능 

cascade 옵션을 활성화해주면 부모 엔티티의 영속성 상태를 변경하면 자식까지 전파된다. 일대다 관계에서 부모가 '일', 자식이 '다'에 해당한다. -> Order에 OrderItem을 set하고 Order만 save해도 Order에 속해있는 OrderItem도 같이 저장된다.

```java
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Orders extends BaseEntity {
    ...
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems;

}
```

## 테스트 코드 작성 시에 LocalDateTime을 조심하자.

```java
public static Orders createOrder(LocalDateTime orderTime, Member member) {
        return Orders.builder()
                .orderStatus(OrderStatus.ORDER)
                .orderTime(orderTime)
                .member(member)
                .orderItems(new ArrayList<>())
                .build();
    }
```
 
orderItem을 파라미터로 받는 이유 ->  LocalDateTime은 테스트 코드를 짤 때, LocalDataTime.now()는 테스트 불가 영역이 된다. 
따라서, 파라미터로 받음으로써 주문시간을 외부에서 주입이 가능, 결과 값을 검증할 때 주문시간도 검증이 가능해진다.

## MainItemImageRepository 위치

메인페이지를 조회하는 쿼리의 경우 메인페이지에 종속된 쿼리이므로 따로 도메인 계층에 저장소를 만들지 않고 web패키지 main에 저장소를 따로 생성한다.

## JPQL을 통해 MainPage의 Item을 가져오기

```java
    @Query(value = "select im " +
        "from ItemImage im join fetch im.item i " +
        "where im.isRepImg = true " +
        "and ( :searchQuery is null or i.itemName like %:searchQuery% or i.itemDetail like %:searchQuery% ) " +
        "and i.itemSellStatus = 'SELL' " +
        "order by i.updatedDate desc",
        countQuery = "select count(im) " +
                "from ItemImage im " +
                "where 1 = 1 " +
                "and   im.isRepImg = true " +
                "and   (:searchQuery is null or im.item.itemName like %:searchQuery% " +
                "            or im.item.itemDetail like %:searchQuery%) " +
                "and   im.item.itemSellStatus = 'SELL' ")
    Page<ItemImage> findMainItemDtoV1(@Param("searchQuery") String searchQuery, Pageable pageable);
        }
```

- fetch join을 이용하여 ItemImage와 연관된 Item을 같이 가져온다.
- where 조건문을 이용하여 대표이미지만 가져온다.
- and 식을 통해 * searchQuery null인 경우 itemName or itemDetail에 searchQuery값이 존재하는 경우에 대한 조건을 추가한다.
- 빠른 시간 순으로 정렬한다.
- fetch join을 이용할 경우 count가 제대로 반응하지 않는 이슈가 있으므로 countQuery를 따로 작성해준다.
- **JPQL를 이용하여 쿼리를 작성하니까 보기 좋지 않다.** => QueryDsl을 이용하자


## MainItemImageRepository에서 MainItemDto를 바로 조회함으로써 성능 최적화

```java
    public Page<MainItemDto> getMainItemPageV2(ItemSearchDto itemSearchDto, Pageable pageable) {
        Page<MainItemDto> mainItemDtos = mainItemImageRepository.findMainItemDtoV2(itemSearchDto.getSearchQuery(), pageable);
        return new PageImpl<>(mainItemDtos, pageable, itemImages.getTotalElements());
    }
…
}

@Service
@RequiredArgsConstructor
public class MainService {

    private final ItemService itemService;
    private final MainItemImageRepository mainItemImageRepository;

   …

    /**
     * mainItemImageRepository에서 MainItemDto를 바로 조회
     */
    public Page<MainItemDto> getMainItemPageV2(ItemSearchDto itemSearchDto, Pageable pageable) {
        Page<MainItemDto> mainItemDtoPage = mainItemImageRepository.findMainItemDtoV2(itemSearchDto.getSearchQuery(), pageable);
        return new PageImpl<>(mainItemDtoPage.getContent(), pageable, mainItemDtoPage.getTotalElements());
    }
}

```

![image](https://user-images.githubusercontent.com/83503188/168557135-c072bc42-f223-4fa8-a88a-401500979bb9.png)
![image](https://user-images.githubusercontent.com/83503188/168557141-167fde8b-7308-445e-a612-bba23225e2b3.png)

- dto에 필요한 값만 SELECT 쿼리에 존재한다.
- count query가 실행되는 경우가 있고 안되는 경우가 있다? -> 실행되지 않는 경우는 총 데이터가 3개인데, 6개씩 가져오는 경우 조회하는 상품의 수가 6개보다 적으므로 count 쿼리가 실행되지 않는 것이다.

fetch join을 이용하여 성능 최적화를 먼저 진행하고, 이후에 더 성능 최적화가 필요하면 dto로 바로 가져오는 방식을 사용하자.

## DTO에 넣는 과정을 of함수를 통해 숨기자.

- 기존 코드 

```java
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemDtlService {

    private final ItemService itemService;
    private final ItemImageService itemImageService;
    private final OrderService orderService;
    private final MemberService memberService;

    public ItemDtlDto getItemDtl(Long itemId) {

        Item item = itemService.getItemById(itemId);
        ItemDtlDto itemDtlDto = ItemDtlDto.of(item);

        List<ItemImage> itemImages = itemImageService.getItemImageByItemId(itemId);
        List<ItemDtlDto.ItemImageDto> itemImageDtos = ItemDtlDto.ItemImageDto.of(itemImages);

        itemDtlDto.setItemImageDtos(itemImageDtos);

        return itemDtlDto;
    }
    ...
}
```

- 변경 후

```java
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemDtlService {

    private final ItemService itemService;
    private final ItemImageService itemImageService;
    private final OrderService orderService;
    private final MemberService memberService;

    public ItemDtlDto getItemDtl(Long itemId) {

        // 1. 상품 정보 조회
        Item item = itemService.getItemById(itemId);

        // 2. 상품 이미지 조회
        List<ItemImage> itemImages = itemImageService.getItemImageByItemId(itemId);

        // 3. 상품 상세 dto 변환
        ItemDtlDto itemDtlDto = ItemDtlDto.of(item, itemImages);
        return itemDtlDto;
    }

}

public class ItemDtlDto {
    ...
    public static ItemDtlDto of(Item item, List<ItemImage> itemImages) {

        List<ItemDtlDto.ItemImageDto> itemImageDtos = ItemDtlDto.ItemImageDto.of(itemImages);
        return ItemDtlDto.builder()
                .itemId(item.getId())
                .itemName(item.getItemName())
                .itemDetail(item.getItemDetail())
                .itemSellStatus(item.getItemSellStatus())
                .price(item.getPrice())
                .deliveryFee(item.getDelivery().getDeliveryFee())
                .stockNumber(item.getStockNumber())
                .itemImageDtos(itemImageDtos)
                .build();
    }
}

```

## 주문에 대해서는 인증된 유저만 가능해야 한다.

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
...

        http.authorizeRequests()
                .antMatchers("/itemdtl/order").authenticated()
                .antMatchers("/", "/login", "/register", "/images/**", "/itemdtl/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        ;
    }
    ...
}
```

- 가장 상단의 인증부터 처리하므로 만약 `antMatchers("/itemdtl/order")`가 2번째에 위치할 경우 인증되지 않은 유저도 주문이 가능해진다.

## Ajax 처리

```java
            $.ajax({
                url      : url,
                type     : "POST",
                contentType : "application/json",
                data     : param,
                beforeSend : function(xhr){
                    /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
                    xhr.setRequestHeader(header, token);
                },
                success  : function(result, status){
                    alert("주문이 완료 되었습니다.");
                    location.href='/orderhist';
                },
                error : function(jqXHR, status, error){
                    if(jqXHR.status == '401'){
                        alert('로그인 후 이용해주세요');
                        location.href='/login';
                    } else{
                        alert(jqXHR.responseText);
                    }
                }
            });

```

- 로그인 안 된 경우 401 오류
- ajax로 요청을 보낼 경우 `x-requested-with` 헤더에 XMLHttpRequest 값이 들어온다. 
```java
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if("XMLHttpRequest".equals(request.getHeader("x-requested-with"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }else {
            response.sendRedirect("/login");
        }
    }
}
```

- 인증이 안 된 경우 HttpServletResponse.SC_UNAUTHORIZED(==401)을 보내는 클래스

## 자바스크립트를 통해 데이터를 보내는 경우 조작이 가능하므로 서버내에서 데이터를 검증하는 과정이 필요하다.

```java
public class OrderDto {
    
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long ItemId;

    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    @NotNull(message = "주문 수량은 필수 입력 값입니다.")
    private int count;
    ...
}
```

## 같은 출고지에서 출발하는 경우는 여러 개의 상품이라도 배송비는 한 번만 적용시킨다.

```java
    public int getTotalDeliveryFee() {
    
        int totalDeliveryFee = 0;
        Map<Long, Delivery> deliveryMap = new HashMap<>();
        for (OrderItem orderItem: orderItems) {
            Delivery delivery = orderItem.getItem().getDelivery();
            deliveryMap.put(delivery.getId(), delivery);
        }

        // 배송비 추가 (배송비 아이디가 같은 경우 출고지가 같은걸로보고 배송비를 1번만 적용함)
        for (Long deliveryId : deliveryMap.keySet()) {
            Delivery delivery = deliveryMap.get(deliveryId);
            totalDeliveryFee += delivery.getDeliveryFee();
        }

        return totalDeliveryFee;
    }
}
```

## 배치 단위로 가져오기

```java
private List<OrderHistDto.OrderItemHistDto> getOrderItemHistDtos(Orders order) {
        List<OrderItem> orderItems = order.getOrderItems();

        List<OrderHistDto.OrderItemHistDto> orderItemHistDtos = orderItems.stream().map(orderItem -> {
            ItemImage itemImage = itemImageService.getItemImageByItemIdAndIsRepImg(orderItem.getItem().getId(), true);
            return OrderHistDto.OrderItemHistDto.of(orderItem, itemImage);
        }).collect(Collectors.toList());
        return orderItemHistDtos;
    }
    
```

해당 코드에서 OrderItem을 가져올 때 yml파일의 `default_batch_fetch_size: 1000`을 통해 orderItem을 배치 단위로 가져올 수 있다. 

![image](https://user-images.githubusercontent.com/83503188/168563357-67f430aa-8dd4-4036-816c-edcdc31629fe.png)

---

## 리팩토링할 부분

1. **주문하기, 주문 취소하기는 현재 web 패키지에 속해있는데 핵심 비즈니스 로직이므로 order패키지에 controller를 하나 만들어서 해당 위치에서 처리할 수 있도록 하자.**
2. ControllerAdvice를 통해 예외 처리
3. 상수 관리하기
4. QueryDsl을 통해 Order처리

