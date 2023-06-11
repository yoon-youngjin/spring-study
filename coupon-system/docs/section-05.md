## 쿠폰을 발급하다가 에러가 발생하면 어떻게 하나요?

### Consumer에서 에러가 발생했을 때 처리하는 방법

현재 시스템에서는 Topic에 존재하는 데이터를 가져간 뒤에 쿠폰을 생성하는 과정에서 오류가 발생한다면, 쿠폰은 실제 생성되지 않았지만, count만 올라가는 문제가 발생할 수 있습니다.
결과적으로 100개보다 작은 수의 쿠폰이 생성되는 문제가 발생할 수 있습니다.

다양한 해결방법이 존재하겠지만 이번 시스템에서는 백업 데이터와 로그를 남기도록 하겠습니다.

```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FailedEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    public FailedEvent(Long userId) {
        this.userId = userId;
    }
}

```

```java
public interface FailedEventRepository extends JpaRepository<FailedEvent, Long> {
}

```

```java
@Component
@RequiredArgsConstructor
public class CouponCreatedConsumer {

    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;
    private final Logger logger = LoggerFactory.getLogger(CouponCreatedConsumer.class);

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        try {
            couponRepository.save(new Coupon(userId));
        } catch (Exception e) {
            logger.error("failed to create coupon::" + userId);
            failedEventRepository.save(new FailedEvent(userId));
        }
    }
}
```

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/b35beece-fd13-40d1-9af6-fbaf1bf3249e)
- 쿠폰 발급 도중에 에러가 발생하면, FailedEvent 테이블에 데이터를 저장한다.
- 이후에 배치 프로그램에서 FailedEvent에 쌓인 데이터들을 주기적으로 읽어서 쿠폰을 발급한다면 결과적으로 100개의 쿠폰을 발급할 수 있다.


