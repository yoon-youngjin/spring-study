## 선착순 쿠폰 System

### 요구사항 정의

선착순 100명에게 할인쿠폰을 제공하는 이벤트를 진행하고자 한다.

이 이벤트는 아래와 같은 조건을 만족해야 한다.
- 선착순 100명에게만 지급되어야한다.
- 101개 이상이 지급되면 안된다.
- 순간적으로 몰리는 트래픽을 버틸 수 있어야 한다.

### 쿠폰발급로직 작성

```java
    public void apply(Long userId) {
        long count = couponRepository.count();

        if (count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }
```

### 문제점 

위 상황은 요청이 하나만 들어오는 경우에는 문제없이 동작한다. 하지만 요청이 동시에 여러번 들어오는 경우에는?

```java
    @Test
    public void 여러명응모() throws InterruptedException {
        // given, when
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서 수행하는 작업을 기다리다록 도와주는 클래스

        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    applyService.apply(userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long count = couponRepository.count();
        
        // then
        assertThat(count).isEqualTo(100);
    }
```

- 1000번의 요청을 동시에 보내는 테스트 
- 1000번의 요청을 동시에 보내도 쿠폰은 100개만 발급되는 것을 기대하지만 테스트에 실패한다.

위와 같은 문제가 발생하는 이유는 쓰레드 사이에 Race Condition 이 발생했기 때문이다. 이러한 문제를 해결하기 위해서는 적절히 동기화해줘야 한다.




