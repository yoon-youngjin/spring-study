# Catalogs and Orders Microservice

### Users Microservice 기능 추가

- 상세 정보 확인, 주문 내역 확인

![image](https://user-images.githubusercontent.com/83503188/193235648-2406fd64-7513-4c7a-bac0-d93d3e1601c9.png)



**UserController**
```java
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;


    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on PORT %s",
                env.getProperty("local.server.port"));
    }

...
}
```

- `env.getProperty("local.server.port")`: 랜덤 포트로 할당된 설정 값을 가져온다.

![image](https://user-images.githubusercontent.com/83503188/193239945-a41014eb-37ed-418d-a70c-66fe9a2f8c02.png)

#### Users Microservice 와 Spring Cloud Gateway 연동

**apigateway-service**

**application.yml**

```yml
server:
...
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user-service/**
```

![image](https://user-images.githubusercontent.com/83503188/193239999-89548ff4-6dd4-4d3b-8891-a3f30a1c09d9.png)

현재 User Service 의 URI 와 API Gateway 의 URI 가 다르다.

![image](https://user-images.githubusercontent.com/83503188/193240587-7215b475-2a0e-4bb3-a28e-0566271deee0.png)

- UserController 의 Root Request Mapping 정보를 변경


```java
@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on PORT %s",
                env.getProperty("local.server.port"));
    }
```

![image](https://user-images.githubusercontent.com/83503188/193241079-3e3b28af-884f-461c-a7e7-663322a91ebc.png)

### User Microservice - 사용자 조회

**ResponseUser**

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {

    private String email;
    private String name;
    private String userId;
    
    private List<ResponseOrder> orders;

}
```

- `@JsonInclude(JsonInclude.Include.NON_NULL)`: 불필요한 값인 null 데이터는 버리고 전달 


**ResponseOrder**

```java
@Data
public class ResponseOrder {

    private String productId;
    private Integer qty;
    private Integer unitPrice; // 단가
    private Integer totalPrice;
    private Date createdAt;
    
    private String orderId;

}
```


**UserDto**

```java
@Data
public class UserDto {
    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createdAt;

    private String encryptedPwd;

    private List<ResponseOrder> orders = new ArrayList<>();
}
```






**UserService**

```java
public interface UserService {

    UserDto create(UserDto userDto);

    UserDto getUserByUserId(String userId);

    List<UserEntity> getUserByAll();

}
```

**UserServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    ...

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("USer not found"));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        List<ResponseOrder> orderList = new ArrayList<>();
        userDto.setOrders(orderList);

        return userDto;
    }

    @Override
    public List<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }
}
```

**UserRepository**

```java
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserId(String userId);

}
```

**UserController**

```java
@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

    ...

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {

        List<UserEntity> userList = userService.getUserByAll();
        List<ResponseUser> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        userList.forEach(v -> result.add(mapper.map(v, ResponseUser.class)));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser result = new ModelMapper().map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
```

![image](https://user-images.githubusercontent.com/83503188/193245724-e1a9f1be-4f33-4852-9e03-de42e7aaafce.png)

![image](https://user-images.githubusercontent.com/83503188/193245761-22dc756b-b878-4171-8e88-1588d19382ce.png)


## Catalogs Microservice 

**Apis**

![image](https://user-images.githubusercontent.com/83503188/193306536-44ec7132-1043-4c2e-9024-948c99d5e04e.png)

**라이브러리 추가**

- Spring Web
- Devtools
- Lombok
- Jpa
- Eureka Discovery Client
- Model Mapper
- h2

**catalog-service**

**application.yml**


```yml
server:
  port: 0

spring:
  application:
    name: catalog-service
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    generate-ddl: true
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb


eureka:
  instance:
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka
logging:
  level:
    dev.yoon.catalogservice: DEBUG

```

- `jpa.hibernate.ddl-auto: create-drop`: 애플리케이션이 기동되면서 초기에 만들어야하는 데이터를 sql파일에 등록해두고 해당 데이터파일을 자동으로 insert해주는 작업을 해줄 수 있다.
- `jpa.show-sql`: sql문 화면에 출력
- `jpa.ddl-auto`: ddl문장 화면에 출력

**CatalogEntity**

```java
@Data
@Entity
@Table(name = "catalog")
public class CatalogEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date createdAt;

}
```

- `@ColumnDefault(value = "CURRENT_TIMESTAMP")`: H2 DB 에서 현재시간을 가져오기 위한 함수이름

**CatalogRepository**

```java
public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {

    CatalogEntity findByProductId(String productId);
}

```






**CatalogDto**

```java
@Data
public class CatalogDto implements Serializable {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId;
    private String userId;
}

```

**ResponseCatalog**

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCatalog {

    private String productId;
    private String productName;
    private Integer unitPrice;
    private Integer stock;
    private Date createdAt;
}
```

**CatalogService**
```java
public interface CatalogService {

    List<CatalogEntity> getAllCatalogs();

}
```

**CatalogServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;


    @Override
    public List<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }

}

```

**CatalogController**

```java
@RequestMapping("catalog-service")
@RestController
@RequiredArgsConstructor
public class CatalogController {


    private final Environment env;
    private final CatalogService catalogService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Catalog Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getUsers() {

        List<CatalogEntity> userList = catalogService.getAllCatalogs();
        List<ResponseCatalog> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        userList.forEach(v -> result.add(mapper.map(v, ResponseCatalog.class)));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



}
```

**API Gateway 에 catalog-service 등록**

```yml
...
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user-service/**
        - id: catalog-service
          uri: lb://CATALOG-SERVICE
          predicates:
            - Path=/catalog-service/**
```

## Orders Microservice

**Apis**

![image](https://user-images.githubusercontent.com/83503188/193314003-12e51242-23c8-42e8-9c7a-7d5818e5e6ef.png)

**라이브러리 추가**

-Spring Web
- Devtools
- Lombok
- Jpa
- Eureka Discovery Client
- Model Mapper
- h2

**order-service**

**application.yml**

```yml
server:
  port: 0

spring:
  application:
    name: order-service
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb


eureka:
  instance:
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka

logging:
  level:
    dev.yoon.orderservice: DEBUG

```

**OrderEntity**

```java
@Data
@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;
    @Column(nullable = false)
    private Integer qty;
    @Column(nullable = false)
    private Integer unitPrice;
    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date createdAt;
}
```

**OrderRepository**

```java
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    OrderEntity findByOrderId(String orderId);
    List<OrderEntity> findByUserId(String userId);
}

```

**OrderDto**

```java
@Data
public class OrderDto {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId;
    private String userId;
}

```

**OrderService**
```java

public interface OrderService {
    OrderDto createOrder(OrderDto orderDetails);

    OrderDto getOrderByOrderId(String orderId);

    List<OrderEntity> getOrdersByUserId(String userId);
}

```

**OrderServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity orderEntity = mapper.map(orderDto, OrderEntity.class);

        orderRepository.save(orderEntity);

        OrderDto returnValue = mapper.map(orderEntity, OrderDto.class);

        return returnValue;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId);
        OrderDto orderDto = new ModelMapper().map(orderEntity, OrderDto.class);

        return orderDto;
    }

    @Override
    public List<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

}
```

**RequestOrder**

```java
@Data
public class RequestOrder {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
}

```

**ResponseOrder**

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOrder {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
    private Date createdAt;

    private String orderId;
}
```

**OrderController**

```java
@RestController
@RequestMapping("/order-service")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        /* jpa */
        OrderDto createdOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        orderList.forEach(v ->
                result.add(mapper.map(v, ResponseOrder.class))
        );

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}

```

**API Gateway 에 order-service 등록**

```yml
...
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user-service/**
        - id: catalog-service
          uri: lb://CATALOG-SERVICE
          predicates:
            - Path=/catalog-service/**
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/order-service/**
```

![image](https://user-images.githubusercontent.com/83503188/193319249-12a8e7a5-cbd1-4497-a1a7-17095830d827.png)
