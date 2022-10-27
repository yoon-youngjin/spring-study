# 재고시스템 만들어보기

재고 시스템을 개발할 때 재고가 맞지않는 문제가 발생할 수 있다.

해당 문제를 **Synchronized**, **Database Lock**, **Redis Distributed Lock**을 이용하여 해결해보자


## 작업환경 세팅

mysql 설치 및 실행

```text
- docker pull mysql: mysql 이미지 다운
- docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=1234 -name mysql mysql: mysql 실행
- docker ps: mysql 실행 확인

```

![image](https://user-images.githubusercontent.com/83503188/186373439-f773288a-21d2-4e84-8e69-d3a19514df3e.png)

mysql 데이터베이스 생성

```text
- docker exec -it mysql bash
- bash-4.4# mysql -u root -p
- mysql> create database stock_example;
- mysql> use stock_example; : 정상적으로 테이블 생성되었는지 확인
```

프로젝트 세팅

```yaml

spring:
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/stock_example
    username: root
    password: 1234

logging:
  level:
    org:
      hibernate:
        SQL: DEBUG
        type:
          descriptor:
            sql:
              BasicBinder: TRACE

```


## 재고감소시스템

Stock Entity

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(Long quantity) {
        if (this.quantity < 0) {
            throw new RuntimeException("foo");
        }

        this.quantity = this.quantity - quantity;
    }


}
```

StockService

```java
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    // 재고 감소
    @Transactional
    public void decrease(Long id, Long quantity) {
        // get stock
        // 재고감소
        // 저장

        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);

    }

}
```

재고감소 Test

```java
@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach // 테스트 전 DB에 데이터 넣는 작업
    public void before() {
        Stock stock = new Stock(1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach // 테스트 후 자동으로 데이터 삭제
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 재고감소() throws Exception {

        stockService.decrease(1L, 1L);

        // 100 - 1 = 99

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(99L, stock.getQuantity());

    }
     @Test
    public void 동시에_100개의_요청() throws Exception {

        int threadCount = 100;

        // ExecutorService: 비동기로 실행하는 작업을 단순하하여 사용할 수 있게 도와주는 자바의 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 요청이 끝날때까지 기다려야하므로 CountDownLatch를 사용
        // CountDownLatch: 다른 스레드에서 수행중인 작업이 모두 완료될 때까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0L, stock.getQuantity());


    }

}
```

## 문제점



![image](https://user-images.githubusercontent.com/83503188/186382279-27574127-6e42-4bb8-9176-b5fb4e99ffda.png)


레이스 컨디션이 일어났기 때문에 발생하는 문제이다.

> 레이스 컨디션?
> 
> 둘 이상의 스레드가 공유 데이터에 엑세스할 수 있고 동시에 변경하려고 할 때 발생하는 문제


기대하는 상황

![image](https://user-images.githubusercontent.com/83503188/186382735-172bd7dc-c9a5-4f02-885b-ab640a64b915.png)

실제 상황

![image](https://user-images.githubusercontent.com/83503188/186382883-784640aa-f22b-440b-8933-07d56e4f6f3f.png)

쓰레드 1이 데이터를 가져가서 갱신하기 전에 쓰레드 2가 값을 가져간다.

쓰레드 1이 데이터를 갱신하고 쓰레드 2도 갱신을 하지만 둘 다 재고가 5인 상태에서 데이터를 가져갔기 때문에 결과적으로 1만 감소하게된다. -> 갱신 누락

위와 같은 문제를 해결하기 위해서는 쓰레드 1의 작업이 완료된 후에 쓰레드 2의 작업이 시작되어야 한다.


