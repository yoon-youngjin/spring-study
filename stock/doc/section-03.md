# Database 이용해보기

Database를 활용하여 레이스컨디션 해결해보기

## Pessimistic Lock (exclusive lock) 

> 실제로 데이터에 Lock을 걸어서 정확성을 맞추는 방법

데이터 변경(update, delete) 시에 사용되는 Lock이다. 조회, 변경유형에 관계없이 다른 트랜잭션이 변경될 데이터에 접근하는 것을 모두 차단한다. 조회만 하는 트랜잭션은 접근이 허용된다.

다른 트랜잭션이 특정 row 의 Lock 을 얻는것을 방지한다.

예를들어 A 트랜잭션이 끝날 때까지 기다렸다가 B 트랜잭션이 Lock을 획득한다.

![image](https://user-images.githubusercontent.com/83503188/186622354-4a163ecf-339b-4502-96c9-96db6ff3b9cc.png)

서버 1이 Lock을 걸고 데이터를 가져가게되면 서버 2, 3, 4, 5는 서버 1이 Lock을 해제하기 전까지 데이터를 가져갈 수 없게 된다.



Pessimistic Lock을 걸게되면 다른 Transaction이 Lock을 해제하기 전까지 데이터를 가져갈 수 없게된다.

![image](https://user-images.githubusercontent.com/83503188/186624742-3f4d5e77-4f6f-4145-b68d-892ccff18e8a.png)

쓰레드 1이 데이터를 가져가고 Lock을 건다. 쓰레드 2가 데이터 획득 시도를 하지만 Lock이 걸려있으므로 대기하게 된다.
쓰레드 1의 작업이 모두 종료되면 쓰레드 2가 데이터에 Lock을 걸고 데이터를 가져가게 된다.

### 구현

```java
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.id =:id")
    Stock findByIdWithPessimisticLock(Long id);
}
```

- `@Lock()` : Spring Data JPA에서는 @Lock 어노테이션을 통해 Pessimistic Lock을 쉽게 구현할 수 있다.

```java
@Test
    public void 동시에_100개의_요청_Pessimistic_Lock() throws Exception {

        int threadCount = 100;

        // ExecutorService: 비동기로 실행하는 작업을 단순하하여 사용할 수 있게 도와주는 자바의 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 요청이 끝날때까지 기다려야하므로 CountDownLatch를 사용
        // CountDownLatch: 다른 스레드에서 수행중인 작업이 모두 완료될 때까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticLockStockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0L, stock.getQuantity());


    }
```

![image](https://user-images.githubusercontent.com/83503188/186626688-de2d7b1b-d317-4fa8-be0a-8984e1dff051.png)

쿼리를 보면 for update를 확인할 수 있는데 해당 부분이 Lock을 걸고 데이터를 가져오는 부분이다.

### 장점
- 충돌이 빈번히 일어난다면 Optimistic Lock보다 성능이 좋을 수 있다.

### 단점
- 별도의 Lock을 가지기 때문에 성능 감소를 생각해야한다.


## Optimistic Lock

> 실제로 Lock을 이용하지 않고 Version을 이용함으로써 정확성을 맞추는 방법

먼저 데이터를 읽은 후에 업데이트를 수행할 때 내가 읽은 버전이 맞는지 확인하며 업데이트를 진행한다.

만약 내가 읽은 버전에서 수정사항이 생긴 경우에는 application에서 다시 읽은 후 작업을 수행하게 된다.

![image](https://user-images.githubusercontent.com/83503188/186622863-988b2def-b0ad-48f3-83f7-87161ab8a669.png)

![image](https://user-images.githubusercontent.com/83503188/186628408-dc61d9d0-94f3-48f1-b1ba-de671e05733c.png)

서버 1과 서버 2이 데이터베이스에서 version이 1인 row를 읽어왔다고 하자.

읽은 후 서버 1이 먼저 update 쿼리를 수행한다면 update query에는 where 절에 version에 대한 조건도 포함시켜 쿼리를 수행한다.

서버 1이 update 쿼리를 수행하여 version을 2로 set하게 된다.

서버 2가 이후에 동일하게 update 쿼리를 수행하게 되는데 조건절에 version 조건이 명시되어 있으므로 업데이트가 수행되지 않는다.

업데이트가 실패하게 되면서 실제 application에서 다시 row를 읽은 후에 작업하라는 로직을 넣어줘야한다.

### 구현

Optimistic Lock을 이용하기 위해서는 Entity에 Version colum을 추가해야한다.
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

    @Version
    private Long version;

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


@Lock 어노테이션을 사용하여 Optimistic Lock을 수행한다.

```java
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from Stock s where s.id =:id")
    Stock findByIdWithOptimisticLock(Long id);
}
```

- `@Lock(value = LockModeType.OPTIMISTIC)` : Spring Data JPA 에서는 @Lock 어노테이션을 통해 Optimistic Lock 을 쉽게 구현할 수 있다.

또한 Optimistic Lock은 업데이트에 실패한 경우 재시도를 해야한다.

```java
@Service
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    @Transactional
    public void decrease(Long id, Long quantity) throws InterruptedException {

        // update 실패 시 50ms 후 재실행하는 로직
        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break;
            }catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

}
```

```java
@Test
    public void 동시에_100개의_요청_Optimistic_Lock() throws InterruptedException {

        int threadCount = 100;

        // ExecutorService: 비동기로 실행하는 작업을 단순하하여 사용할 수 있게 도와주는 자바의 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 요청이 끝날때까지 기다려야하므로 CountDownLatch를 사용
        // CountDownLatch: 다른 스레드에서 수행중인 작업이 모두 완료될 때까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0L, stock.getQuantity());


    }
```


### 장점

- 별도의 Lock을 잡지 않으므로 Pessimistic Lock보다 성능상 이점이 있다.

### 단점

- 업데이트가 실패했을 때 재시도 로직을 개발자가 직접 명시해줘야 한다.
- 충돌이 빈번히 일어난다면 Pessimistic Lock을 이용하는 것이 성능상 이점이 있을 수 있다.

## Named Lock

> 이름을 가진 Metadata lock, 이름을 가진 Lock을 획득한 후 해제될 때까지 다른 세션은 해당 Lock을 획득할 수 없다.

이름과 함께 Lock을 획득한다. 해당 lock 은 다른세션에서 획득 및 해제가 불가능하다.

주의할점은 Transaction이 종료될 때 Lock이 자동으로 해제되지 않기 때문에 별도의 명령어로 해제해주거나 선점시간이 끝나야 해제된다.

Mysql에서는 getLock명령어를 통해 Lock을 획득할 수 있고 Release명령어를 통해 Lock을 해제할 수 있다.

![image](https://user-images.githubusercontent.com/83503188/186632095-4f6b96ee-aff3-4621-af64-e0c2a30c2c5e.png)


Pessimistic Lock의 경우에는 Stock 데이터에 Lock을 걸지만 Named Lock은 Stock에는 Lock을 걸지 않고 별도의 공간에 Lock을 건다.

Session 1이 '1'이라는 이름으로 Lock을 건다면 다른 Session에서는 Session 1이 Lock을 해제한 후에 획득할 수 있게된다.

### 구현
실습에서는 편의성을 위해 JPA의 native query를 이용할 것이고, 동일한 데이터 소스를 사용한다.

실제로 사용할 때는 데이터 소스를 분리하여 사용해야 한다.

같은 데이터소스를 사용하게 된다면 커넥션 풀이 부족해지게되어 다른 서비스에서 영향을 끼칠 수 있다.

```java
// 편의성을 위해 Stock Entity를 사용한다.
// 실무에서는 별도의 JDBC를 사용하거나 등등의 방식을 사용해야한다.
public interface LockRepository extends JpaRepository<Stock, Long> {

    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);

}
```

실제 로직 전 후로 getLock, releaseLock을 수행해야 하므로 facade클래스 추가

```java
@Component
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;


    @Transactional
    public void decrease(Long id, Long quantity) throws InterruptedException {

        try {
            lockRepository.getLock(id.toString());
            stockService.decrease(id, quantity);
        }finally {
            lockRepository.releaseLock(id.toString());

        }
    }

}
```

StockService는 부모의 transaction과 별도로 수행되어야 하므로 propagation 변경한다.

```java
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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



같은 데이터 소스를 사용할 것이므로 커넥션풀을 늘려준다.

```yaml
spring:
    hikari:
      maximum-pool-size: 40
```

```java
@Test
    public void 동시에_100개의_요청_Named_Lock() throws InterruptedException {

        int threadCount = 100;

        // ExecutorService: 비동기로 실행하는 작업을 단순하하여 사용할 수 있게 도와주는 자바의 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 요청이 끝날때까지 기다려야하므로 CountDownLatch를 사용
        // CountDownLatch: 다른 스레드에서 수행중인 작업이 모두 완료될 때까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    namedLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0L, stock.getQuantity());


    }
```


Named Lock은 주로 분산 Lock을 구현할 때 사용한다.

Pessimistic Lock은 timeout을 구현하기 힘들지만 Named Lock은 손쉽게 구현이 가능하다.

하지만 Named Lock은 Transaction 종료 시에 Lock해제와 Session 관리를 잘해줘야하므로 주의해야하고 실제 구현 시 복잡해질 수 있다.
