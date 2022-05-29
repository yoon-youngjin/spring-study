## refresh token에 대한 예외를 추가하자.

Bearer Authentication으로 받을 수 있는 token이 access token뿐만 아니라 refresh token이 들어올 수도 있기 때문에 이에 대한 예외 처리를 추가하자.

---

## 인증 인터셉터 구현

Interceptor의 경우 dispatcher servlet과 controller 사이에서 동작한다.

Handlerinterceptor를 구현한 구현체인 AuthenticationInterceptor 구현, Handlerinterceptor에는 preHandle, postHandle, afterCompletion 메소드가 존재

- prehandle: controller 실행 전에 동작하는 함수
- postHandle: controller 실행 후에 동작하는 함수
- afterCompletion: 마지막으로 동작하는 함수

postHandler과의 차이점은 controller를 수행 중에 오류가 발생하는 경우 postHandle 메소드는 동작하지 않고 afterCompletion은 오류가 발생해도 수행

### WebConfig에 interceptor 등록

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authenticationInterceptor)
                .order(1)
                .excludePathPatterns("/login", "/auth/kakao/callback", "/api/health", "/api/oauth/login", "/api/logout", "/api/token") // 해당 경로는 인터셉터가 가로채지 않는다.
                .addPathPatterns("/api/**");

        registry.addInterceptor(adminInterceptor)
                .order(2)
                .addPathPatterns("/api/admin/**");

    }
}
```

- filter chain 방식으로 수행
- order 메소드를 통해 순서를 지정할 수 있다.

### AuthenticationInterceptor

```java
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenValidator tokenValidator;

    private final TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("AuthenticationInterceptor preHandle");

        // 정상적인 토큰인지 검증 -> 토큰 유무 확인, Bearer Grant Type 확인
        String authorization = request.getHeader("Authorization");
        tokenValidator.validateAuthorization(authorization);

        String accessToken = authorization.split(" ")[1];

        // 가져온 토큰이 해당 서버에서 발급한 JWT인지 검증
        if (!tokenManager.validateToken(accessToken)) {
            throw new AuthenticationException(ErrorCode.NOT_VALID_TOKEN);
        }

        // 토큰이 refresh token인지 검증
        Claims tokenClaims = tokenManager.getTokenClaims(accessToken);
        if (!StringUtils.equals(TokenType.ACCESS.name(), tokenClaims.getSubject())) {
            throw new AuthenticationException(ErrorCode.NOT_ACCESS_TOKEN_TYPE);
        }

        // 액세스 토큰 만료 시간 검증
        if (tokenManager.isTokenExpired(tokenClaims.getExpiration())) {
            throw new AuthenticationException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        }

        return true;

    }
    ...
}
```

## 인가 인터셉터 구현


```java
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthorizationInterceptor implements HandlerInterceptor {

    private final TokenManager tokenManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("AuthenticationInterceptor preHandle");

        String authorization = request.getHeader("Authorization");

        String accessToken = authorization.split(" ")[1];

        Claims tokenClaims = tokenManager.getTokenClaims(accessToken);
        String role = (String) tokenClaims.get("role");
        Role roleEnum = Role.valueOf(role);

        if (!Role.ADMIN.equals(roleEnum)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_ADMIN);
        }

        return true;
    }
    ...
}
```

인가의 경우 대부분 403 Forbidden 에러 발생 처리


### Interceptor 실행 순서

![image](https://user-images.githubusercontent.com/83503188/170824488-ab3cebd4-29cd-4b23-8494-89c7266a564e.png)


## 상품 수정

### 중첩 클래스를 통한 request, response 관리

```java
public class UpdateItemDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Request {

        @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
        private Long itemId;

        @NotBlank(message = "상품명은 필수 입력 값입니다.")
        private String itemName;

        @NotNull(message = "가격은 필수 입력 값입니다.")
        private Integer price;

        private String itemDetail;

        @NotNull(message = "재고는 필수 입력 값입니다.")
        private Integer stockNumber;

        @NotNull(message = "판매상태는 필수 입력 값입니다. (SELL OR SOLD_OUT)")
        private ItemSellStatus itemSellStatus;

        @NotNull(message = "배송 정보는 필수 입력 값입니다.")
        private Long deliveryId;

        public Item toEntity() {
            return Item.builder()
                    .itemName(itemName)
                    .price(price)
                    .itemDetail(itemDetail)
                    .stockNumber(stockNumber)
                    .itemSellStatus(itemSellStatus)
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {

        private Long itemId;

        private String itemName;

        private Integer price;

        private String itemDetail;

        private Integer stockNumber;

        private ItemSellStatus itemSellStatus;

        private Long deliveryId;

        public static Response of(Item item) {
            return UpdateItemDto.Response.builder()
                    .itemId(item.getId())
                    .itemName(item.getItemName())
                    .itemDetail(item.getItemDetail())
                    .itemSellStatus(item.getItemSellStatus())
                    .stockNumber(item.getStockNumber())
                    .price(item.getPrice())
                    .deliveryId(item.getDelivery().getId())
                    .build();
        }
    }


}
```

### PatchMapping vs. PutMapping 

- PatchMapping: 부분 업데이트
- PutMapping: 전체 업데이트
  - 예를 들어, 상품 아이디로 조회 시 상푸이 없을 경우 상품 생성도 하고 상품이 있는 경우 해당 상품 데이터를 전부 변경하는 용도

## Resolver

Access Token에 있는 이메일 정보를 가져오기 위해서는 header를 통해 token을 가져오고 token manager를 통해 parsing하는 과정을 겨쳤다. 
argument resolver를 사용하면 사용자 정의 어노테이션을 통해 깔끔하게 정리할 수 있다.

### WebConfig에 Resolver 추가

```java
@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    ...
    
    private final MemberEmailArgumentResolver memberEmailArgumentResolver;

    ...
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberEmailArgumentResolver);
    }
    ...
}
```

### 사용자 정의 어노테이션 생성

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberEmail {
}

```

### Resolver 생성

HandlerMethodArgumentResolver를 구현

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberEmailArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenManager tokenManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasEmailAnnotation = parameter.hasParameterAnnotation(MemberEmail.class); // 1)
        boolean hasString = String.class.isAssignableFrom(parameter.getParameterType()); // 2)
        return hasEmailAnnotation && hasString;

    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory
        ) throws Exception {
            HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            token = token.split(" ")[1];
            return tokenManager.getMemberEmail(token);
        }
    }
}
```

supportParameter에서 해당 파라미터를 검증하고 검증을 통과하면 resolveArgument를 수행 

1. MemberEmail이 붙은 경우
2. 해당 어노테이션이 parameter에 존재하는 하고 해당 파라미터가 String인 경우

![image](https://user-images.githubusercontent.com/83503188/170824839-44a23705-a96e-43bb-b4e1-82c8597ddfe5.png)
![image](https://user-images.githubusercontent.com/83503188/170824850-2f66636a-d877-42b1-b3e3-ab0677caf97d.png)


## ControllerTest

```java
@ActiveProfiles("test") // 1) 
@ExtendWith(MockitoExtension.class) // 2) 
class ApiItemControllerTest {

    /**

     */
    @InjectMocks // 3)
    private ApiItemController apiItemController;

    @Mock // 4)
    private ApiItemService apiItemService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
        void beforeEach() {
        /**
         * MockMvcBuilders를 통해
         * 스프링 없이 단위 테스트를 작성할 수 있다.
         * 테스트 속도가 빨라진다.
         */
            mockMvc = MockMvcBuilders.standaloneSetup(apiItemController)
                    .build();
    }

    @Test
    @DisplayName("상품 수정 테스트")
    public void 상품_수정_테스트() throws Exception {

        // given -> 상황이 주어짐
        Long itemId = 1L;
        UpdateItemDto.Request updateItemRequestDto = UpdateItemDto.Request.builder()
                .itemId(itemId)
                .itemDetail("테스트 상품 상세")
                .itemName("테스트 상품명")
                .itemSellStatus(ItemSellStatus.SELL)
                .price(30000)
                .stockNumber(3)
                .deliveryId(2L)
                .build();

        UpdateItemDto.Response updateItemResponseDto = UpdateItemDto.Response.builder()
                .itemId(itemId)
                .itemDetail("테스트 상품 상세")
                .itemName("테스트 상품명")
                .itemSellStatus(ItemSellStatus.SELL)
                .price(30000)
                .stockNumber(3)
                .deliveryId(2L)
                .build();

        
        Mockito.when(apiItemService.updateItem(itemId, updateItemRequestDto)).thenReturn(updateItemResponseDto); // 5)

        // when -> 해당 동작
        String json = objectMapper.writeValueAsString(updateItemResponseDto);

        final ResultActions actions = mockMvc.perform(
                patch("/api/admin/items/{itemId}", itemId)
                        .accept(MediaType.APPLICATION_JSON) // 받는 타입
                        .contentType(MediaType.APPLICATION_JSON) // 전송하는 타입
                        .content(json));

        // then -> 결과
        actions .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemId", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemName", is(updateItemResponseDto.getItemName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemDetail", is(updateItemResponseDto.getItemDetail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", is(updateItemResponseDto.getPrice())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stockNumber", is(updateItemResponseDto.getStockNumber())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemSellStatus", is(updateItemResponseDto.getItemSellStatus().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deliveryId", is(2)))
                .andDo(print());


    }
}

```
단위 테스트 진행, ApiItemController에서는 어떤 요청이 오면 예상대로 응답을 하는지만 검증하는 것이기 때문에 테스트 대상이 아닌 ApiItemService는 실제로 객체를 넣는 것이 아닌 어떤 결과를 반환하는지만 명시(=given)해주는 것

1. 설정파일을 application-test.yml로 실행
2. Mocking 라이브러리를 사용함을 알려주는 어노테이션
3. ApiItemController에서 주입하고 있는 ApiItemService를 Mock 객체로 주입을 해줄 수 있다.
4. 주입 받을 Mock 객체
5. 테스트 대상이 아닌 apiItemService는 mocking 처리 -> 동작만 정의해준다.

> 실패가 일어난다. why?
> 
> updateItemRequestDto를 받으면 updateItemResponseDto를 응답한다고 정의했는데 objectMapper를 통해 json으로 바꾼 뒤 httpbody에 넣어주는 중 
> controller에서는 httpbody의 내용을 dto 객체로 만들어서 updateItem에 넘겨주고 있다.
> 즉, 두 객체는 주소가 같지 않음 -> Memory 주소 값을 동일하게 하기 위해서 UpdateItemRequestDto.Request에 `@EqulasAndHashCode` 어노테이션을 통해 필드의 값을 가지고 같은 객체임을 판단할 수 있다.


## ApiServiceTest

```java
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ApiItemServiceTest {

    @InjectMocks
    private ApiItemService apiItemService;

    @Mock
    private ItemService itemService;

    @Mock
    private DeliveryService deliveryService;

    @Test
    @DisplayName("상품 수정 테스트")
    public void 상품_수정_테스트() throws Exception {

        // given
        Long itemId = 1l;
        Long deliveryId = 1l;

        Delivery delivery = Delivery.builder()
                .id(deliveryId)
                .deliveryFee(3000)
                .deliveryName("마포구 물류센터")
                .build();

        UpdateItemDto.Request updateItemRequestDto = UpdateItemDto.Request.builder()
                .itemId(itemId)
                .itemName("업데이트 상품")
                .itemSellStatus(ItemSellStatus.SELL)
                .itemDetail("업데이트 상품 상세")
                .price(3000)
                .stockNumber(100)
                .deliveryId(deliveryId)
                .build();

        Item savedItem = Item.builder()
                .itemName(updateItemRequestDto.getItemName())
                .itemSellStatus(ItemSellStatus.SELL)
                .itemDetail(updateItemRequestDto.getItemDetail())
                .price(3000)
                .stockNumber(100)
                .build();

        savedItem.updateDelivery(delivery);

        Item updateItem = updateItemRequestDto.toEntity();
       
        Mockito.when(itemService.updateItem(itemId, updateItem)).thenReturn(savedItem);
        Mockito.when(deliveryService.getDeliveryById(updateItemRequestDto.getDeliveryId())).thenReturn(delivery);

        // when
        UpdateItemDto.Response response = apiItemService.updateItem(itemId, updateItemRequestDto);

        // then
        Assertions.assertThat(response.getItemName()).isEqualTo(updateItemRequestDto.getItemName());
        Assertions.assertThat(response.getItemSellStatus()).isEqualTo(updateItemRequestDto.getItemSellStatus());
        Assertions.assertThat(response.getItemDetail()).isEqualTo(updateItemRequestDto.getItemDetail());
        Assertions.assertThat(response.getPrice()).isEqualTo(updateItemRequestDto.getPrice());
        Assertions.assertThat(response.getStockNumber()).isEqualTo(updateItemRequestDto.getStockNumber());

    }

}
```

- **mocking 처리한 부분에 대한 단위 테스트 코드도 존재해야 한다.**
- itemServiceTest, deliveryServiceTest도 추가로 작성
- 또한 해당 serviceTest에서 사용한 entity, repository에 대한 테스트도 추가

## ServiceTest

```java
package com.shop.projectlion.domain.delivery.service;

import com.shop.projectlion.domain.delivery.domain.Delivery;
import com.shop.projectlion.domain.delivery.repository.DeliveryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("배송 정보 get 테스트")
    public void 배송_정보_get_테스트() throws Exception {

        // given
        Long deliveryId = 1l;

        Delivery delivery = Delivery.builder()
                .id(deliveryId)
                .deliveryFee(3000)
                .deliveryName("마포구 물류센터")
                .build();

        Mockito.when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        // when
        Delivery foundDelivery = deliveryService.getDeliveryById(deliveryId);

        // then
        Assertions.assertThat(foundDelivery.getDeliveryName()).isEqualTo(delivery.getDeliveryName());
        Assertions.assertThat(foundDelivery.getDeliveryFee()).isEqualTo(delivery.getDeliveryFee());

    }

}
```

### RepositoryTest


```java
@ActiveProfiles("test")
@DataJpaTest // 1) 
class DeliveryRepositoryTest {

    @Autowired // 2)
    private MemberRepository memberRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("배송 조회 테스트")
    void findByMember() {

        // given -> 메모리 데이터베이스에 임의의 값을 넣어주고 시작해야한다.
        Member member = Member.builder()
                .email("test@test.com")
                .memberType(MemberType.GENERAL)
                .role(Role.ADMIN)
                .memberName("홍길동")
                .build();
        Member savedMember = memberRepository.save(member);

        Delivery delivery = Delivery.builder()
                .deliveryName("마포구 물류센터")
                .deliveryFee(3000)
                .member(member)
                .build();
        deliveryRepository.save(delivery);

        // when
        Optional<List<Delivery>> deliveries = deliveryRepository.findByMember(savedMember);
        Delivery savedDelivery = deliveries.get().get(0);

        // then
        Assertions.assertThat(savedDelivery.getId()).isNotNull();
        Assertions.assertThat(savedDelivery.getDeliveryName()).isEqualTo(delivery.getDeliveryName());
        Assertions.assertThat(savedDelivery.getDeliveryFee()).isEqualTo(delivery.getDeliveryFee());
    }

}
```

메모리 데이터베이스와 연동하여 테스트를 진행해야 한다. -> @DataJpaTest

1. 데이터베이스와 관련된 객체만 bean으로 등록하기 위한 어노테이션
2. repositoryTest의 경우 Mock 객체가 아닌 실제 객체를 주입받는다.

### EntityTest


```java
class ItemTest {

    @Test
    @DisplayName("상품 업데이트 테스트")
    void updateItem() {
        // given
        Item item = Item.builder()
                .itemName("상품명")
                .itemSellStatus(ItemSellStatus.SOLD_OUT)
                .itemDetail("상품 상세")
                .price(5000)
                .stockNumber(50)
                .build();

        Item updateItem = Item.builder()
                .itemName("업데이트 상품명")
                .itemSellStatus(ItemSellStatus.SELL)
                .itemDetail("업데이트 상품 상세")
                .price(1000)
                .stockNumber(100)
                .build();

        // when
        item.updateItem(updateItem);

        // then
        Assertions.assertThat(item.getItemName()).isEqualTo(updateItem.getItemName());
        Assertions.assertThat(item.getItemSellStatus()).isEqualTo(updateItem.getItemSellStatus());
        Assertions.assertThat(item.getItemDetail()).isEqualTo(updateItem.getItemDetail());
        Assertions.assertThat(item.getPrice()).isEqualTo(updateItem.getPrice());
        Assertions.assertThat(item.getStockNumber()).isEqualTo(updateItem.getStockNumber());
    }
}
```

## CI/CD 과정 정리



---

### Refactoring

**상품 등록 API 개발**

