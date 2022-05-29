# 2주차 과제 피드백

## 실제 현업에서는 데이터 컬럼이 매우 많으므로 현실적으로 모든 데이터를 파라미터로 받는 것이 아닌 엔티티 자체로 받는 방법을 고민하자.

```java
public void itemUpdate(String itemName, ItemSellStatus itemSellStatus, Integer price, Integer stockNumber, String itemDetail){
        this.itemName = itemName;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
        }
```

```java
public void itemUpdate(Item updateItem) {
        this.itemName = updateItem.getItemName();
        this.price = updateItem.getPrice();
        this.stockNumber = updateItem.getStockNumber();
        this.itemDetail = updateItem.getItemDetail();
        this.itemSellStatus = updateItem.getItemSellStatus();
        }
```

## Web화면에 최적화된 컨트롤러(`AdminItemController`) 같은 경우 너무 많은 서비스를 의존하지 말고 `AdminItemService`를 만들어서 데이터 조회 결과 같은것을 받자.

```java
public class AdminItemController {

    private final ItemService itemService;
    private final AdminItemService adminItemService;
    private final DeliveryService deliveryService;
    private final ItemImageService itemImageService;
    ...
}
```

```java
public class AdminItemController {
    private final AdminItemService adminItemService;
    ...
}
```
## Dto를 Entity로 만드는 toEntity 메소드를 활용하자.
```java
public class UpdateItemDto {
    ...

    public Item toEntity() {
        return Item.builder()
                .itemName(itemName)
                .itemDetail(itemDetail)
                .price(price)
                .itemSellStatus(itemSellStatus)
                .stockNumber(stockNumber)
                .build();
    }
    ...
}
```

## Entity를 인자로 받아서 Dto에 값을 넣는 of 메소드를 활용하자.

```java
@Builder
@Getter @Setter
public class DeliveryDto {
    private Long deliveryId;
    private String deliveryName;
    private int deliveryFee;
    
    public static DeliveryDto of(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getId())
                .deliveryName(delivery.getDeliveryName())
                .deliveryFee(delivery.getDeliveryFee())
                .build();
    }
}
```

## hasRole() 작동 X

- hasRole이 작동을 안하길래 hasAutority를 통해 해결
- Spring Security는 역할(Role)과 권한(Authority)을 구조적으로 분리하지 않고, UserDetails#getAuthorities() 로 획득 가능한 GrantedAuthority 인터페이스 구현체의 목록으로 설계하였다. 다만, 역할의 경우 문자열 앞에 ROLE_을 붙이도록 하여, 권한과 구분되도록 설계하였다. 개발자는 실제 구현시 역할과 권한을 별도의 저장소로 분리하여 1:N의 관계로 설계하고, UserDetails 구현체 작성시에는 해당 사용자에게 부여된 역할과 권한을 모두 문자열로 UserDetails#getAuthorities() 을 통해 획득되도록 구현하면 된다.
- hasRole()은 메소드 내부에서 ROLE 접두어를 붙이고 해당 역할이 있는지를 찾고, hasAuthority()는 입력한 문자열 그대로 해당 권한을 찾도록 되어있다.

- 기존 코드 
```java
public UserDetailsImpl(Member member) {
        this.member = member;
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
    }

```

- 변경 후
```java
public UserDetailsImpl(Member member) {
        this.member = member;
        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
        }
```



---

### `@ModelAttribute` 어노테이션을 활용하여 반복적으로 Model에 데이터를 넣는 작업을 처리하자

- [x] 기존 작업
```java

@GetMapping("/new")
public String itemForm(Model model) {
        String email = userDetails.getUsername();
        List<DeliveryDto> deliveryDtos = adminItemService.getMemberDeliveryDtos(email);
        model.addAttribute("insertItemDto", new InsertItemDto());
        model.addAttribute("deliveryDtos", deliveryDtos)
        return "adminitem/registeritemform";
        }
        
@GetMapping("/{itemId}")
public String itemEdit(@PathVariable Long itemId, Model model) {
        String email = userDetails.getUsername();
        List<DeliveryDto> deliveryDtos = adminItemService.getMemberDeliveryDtos(email);
        UpdateItemDto updateItemDto = adminItemService.getUpdateItemDto(itemId);
        model.addAttribute("updateItemDto", updateItemDto);
        model.addAttribute("deliveryDtos", deliveryDtos)
        return "adminitem/updateitemform";
    }
```

모든 View에 Delivery 정보가 필요하므로 같은 작업을 Delivery를 가져와서 Model에 넣어주는 작업을 반복하고 있다.

- [x] 변경 후

```java
@ModelAttribute("deliveryDtos")
public List<DeliveryDto> deliveryDtos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = userDetails.getUsername();
        List<DeliveryDto> deliveryDtos = adminItemService.getMemberDeliveryDtos(email);
        return deliveryDtos;
        }

@GetMapping("/new")
public String itemForm(Model model){
        model.addAttribute("insertItemDto",new InsertItemDto());
        return"adminitem/registeritemform";
        }

@GetMapping("/{itemId}")
public String itemEdit(@PathVariable Long itemId, Model model) {
        UpdateItemDto updateItemDto = adminItemService.getUpdateItemDto(itemId);
        model.addAttribute("updateItemDto", updateItemDto);
        return "adminitem/updateitemform";
        }
```

기존에 Delivery를 가져와서 Dto로 변경한 뒤 Model에 넣던 작업을 `@ModelAttribute` 어노테이션으로 처리하였다.

### RedirectAttributes 객체를 통해 리다이렉트를 처리하자

```java
@PostMapping("/{itemId}")
    public String itemEdit(
            @PathVariable Long itemId,
            @Valid UpdateItemDto updateItemDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        ...
        redirectAttributes.addAttribute("itemId", itemId);
        return "redirect:/admin/items/{itemId}";
    }
```

### `@AuthenticationPrincipal` 어노테이션

- 로그인한 사용자의 정보를 파라미터로 받고 싶을 때 기존에는 Principal 객체로 받아서 사용한다. 하지만 이 객체는 SecurityContextHolder의 Principal와는 다른 객체이다.
  - name 정보 밖에 없다.
- `@AuthenticationPrincipal` 어노테이션을 사용하면 **UserDetailsService**에서 Return한 객체 를 파라메터로 직접 받아 사용할 수 있다.

```java
@ModelAttribute("deliveryDtos")
    public List<DeliveryDto> deliveryDtos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String email = userDetails.getUsername();
        List<DeliveryDto> deliveryDtos = adminItemService.getMemberDeliveryDtos(email);
        return deliveryDtos;
    }
```

### BusinessException을 직접 사용하지 않고 BusinessException을 상속 받은 EntityNotFoundException을 사용하는 이유

예외를 모니터링하는 과정에서 BusinessException은 로직을 수행하다가 에러가 나야하는 상황이면 BusinessException을 던지는데 해당 경우는 실제 에러로 잡지 않고 EntityNotFoundException은 데이터가 있어야 하는데 없는 경우 어딘가의 데이터의 정확성이 틀어진 경우이므로 EntityNotFoundException을 모니터링하면 어디서 문제가 생긴 것인지 파악하기 쉽다.

### 상품 이미지 수정 과정

![image](https://user-images.githubusercontent.com/83503188/167259706-a33c9684-f694-4520-b08c-d4cdd32d167a.png)

### 계층형 패키지 구조 vs. 도메인 패키지 구조

![image](https://user-images.githubusercontent.com/83503188/167259739-c0878861-8135-49a2-b619-4e5f4e9aebd9.png)
![image](https://user-images.githubusercontent.com/83503188/167259740-b0fb0e8f-33ba-4871-bbe2-b374c44d645b.png)
![image](https://user-images.githubusercontent.com/83503188/167259754-838410c0-bee8-4a3d-88f8-ce836a0f9863.png)

- web 패키지에서 domain 패키지의 내용을 가져와서 사용
- domain 패키지에서는 web 패키지의 존재를 모르도록 설계

### Fetch Join / @QueryProjection 활용
![image](https://user-images.githubusercontent.com/83503188/167259815-98f539d5-00cc-40de-aa85-44d2ad49dd52.png)


