## Redis를 활용하여 문제 해결하기

### 문제점 해결하기

앞 섹션에서 Race Condition 이 발생하는 부분은 쿠폰 개수를 가져오는 부분(`long count = couponRepository.count();`)이었습니다.

Race Condition 여러 스레드에서 동시에 공유 자원에 접근할 때 발생하므로 싱글 스레드로 공유 자원에 접근한다면 Race Condition는 발생하지 않을 것입니다. 

하지만 쿠폰 발급 로직 전체를 싱글 스레드로 동작시킨다면 성능이 좋지 않을 것입니다. 
따라서 처음 고민해볼 솔루션은 자바에서 제공하는 동기화 도구를 사용해볼 수 있겠지만 이는 Scale Out된 상황을 가정하면 다시 문제가 발생합니다.
다음으로 MySQL, Redis Lock를 활용하는 방법도 있습니다. 하지만 현재 원하는 것은 쿠폰 개수에 대한 정합성인데 Lock을 활용하여 구현한다면 발급된 쿠폰 개수를 가져오는 것부터 쿠폰을 생성할 때까지 Lock을 걸어야하므로 성능상 좋지 않습니다.

Redis에는 incr이라는 명령어가 존재하고, 이는 key에 대한 value를 1씩 증가시키는 명령어입니다. 
Redis는 싱글 스레드로 동작하기 때문에 Race Condition을 해결할 수 있을 뿐만 아니라 incr 명령어는 성능도 굉장히 빠른 명령어입니다.
해당 명령어를 사용하여 쿠폰 개수를 제어한다면 성능도 빠르며, 데이터 정합성도 지킬 수 있습니다.

incr 명령어는 key에 대한 value를 1씩 증가시키고 증가된 값을 리턴하는 명령어이므로, 쿠폰을 발급하기 전에 `coupon_count`를 1증가 시키고, 리턴되는 값이 100보다 크다면, 이미 100개가 발급되었다는 의미이므로 더 이상 발급되서는 안된다.

**CouponCountRepository**
```java
@Repository
@RequiredArgsConstructor
public class CouponCountRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Long increment() {
        return redisTemplate
                .opsForValue()
                .increment("coupon_count");
    }
}
```

**ApplyService**
```java
@Service
@RequiredArgsConstructor
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;

    public void apply(Long userId) {
        Long count = couponCountRepository.increment();
//        long count = couponRepository.count();

        if (count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }
}
```

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/7ca23330-f4d9-4427-bf3f-7b0264bef3c8)
- Thread 1이 10:00시에 쿠폰 개수를 증가시키는 명령어를 실행하여 10:02에 완료되는 경우
- Thread 2가 10:01에 쿠폰 개수를 증가시키는 명령어를 실행한다면 Redis는 싱글 스레드이므로 Thread 1이 실행한 쿠폰 개수 증가가 완료될 때까지 기다렸다가 10:02에 작업을 실행합니다.
- 따라서 모든 Thread는 언제나 최신값을 가져갈수 있기 때문에 쿠폰이 100개보다 많이 생성되는 문제는 발생하지 않습니다.

### 문제점

현재 방식은 쿠폰 발급 요청이 들어오면 Redis를 활용해서 현재 발급된 쿠폰의 개수를 가져온 후에 발급이 가능하다면 RDB에 저장하는 방식입니다.
이 방식은 발급하는 쿠폰의 개수가 많아지면 많아질수록 RDB에 부하를 주게 됩니다. 만약 해당 RDB가 쿠폰 전용 DB가 아니라 다양한 곳에서 사용중이라면 다른 서비스까지 장애를 야기할 수 있습니다.

#### 이유 

**Mysql이 1분에 100개의 insert가 가능하다고 가정** 

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/c859d9ee-0901-4d01-a6ca-d8dc2fc533e1)

위와 같은 상황이라면 1분에 100개씩 10000개를 생성하려면 100분이 걸리게 됩니다.
10:01에 들어온 주문생성 요청, 10:02에 들어온 회원가입 요청은 100분 이후에 진행됩니다. timeout이 걸려있지 않다면 느리게라도 수행이 되겠지만 대부분에는 timeout이 걸려있으므로 아래 요청이 실패하는 상황이 발생할 수 있습니다.

또한, 짧은 시간내에 많은 요청이 들어온다면 DB 서버에 리소스를 많이 사용하므로 부하가 발생하게되고 이는 곳 서비스 지연 혹은 오류로 이어질 수 있습니다.


