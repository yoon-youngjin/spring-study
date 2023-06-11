## 요구사항 변경

현재까지 작성한 코드는 한명이 쿠폰 여러 개를 발급 받을 수 있습니다.
하지만, 대부분의 선착순 이벤트는 1인당 1개로 개수를 제한하는 경우가 많습니다. 그래서 이번에는 1인당 발급가능한 개수를 1개로 제한하는 요구사항을 추가합니다.

### 1번 해결방법
위 요구사항을 만족하기 위한 가장 쉬운 받법은 DB에 Unique 키를 사용하는 것입니다.
Coupon 테이블에 userId와 couponType 컬럼을 추가한 뒤, userId와 couponType에 Unique 키를 설정함으로써 1개만 생성되도록 DB 레벨에서 막을 수 있습니다.
하지만 보통 서비스는 1명의 유저가 같은 타입의 쿠폰을 여러 개 가질 수 있기 때문에 실용적인 방법이 아닙니다.

### 2번 해결방법

다음 방법으로 `apply()` 를 Lock으로 설정하고, 쿠폰발급 여부를 파악하는 것입니다. 
하지만, 현재 상황은 api 모듈에서는 쿠폰발급 가능 여부만 파악하고, consumer 모듈에서 쿠폰을 생성하고 있습니다. 사이에 시간차가 존재하므로 Lock을 건다고 하더라도 1명이 2개의 쿠폰을 발급받을 수 있습니다.

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/0b2fa14c-e6bd-48df-a91a-0a2f9c74585f)
- User1이 API로 최초 쿠폰 발급 요청을 보낸다.
- Lock을 걸고, 쿠폰 발급 여부를 파악한 뒤, 아직 발급한적이 없다면 Topic에 쿠폰 발급 요청을 담고, API는 결과값을 리턴
- 이후에 Consumer는 Topic으로 부터 쿠폰 발급 요청을 가져가서 쿠폰 발급(생성) 준비를 한다.
- 실제로 쿠폰을 발급하기 전에 User1이 다시 발급 요청을 보내게 된다면 Topic에 다시 쿠폰 발급 요청이 담기게 된다.

또한, 메서드 자체를 Lock을 걸기 때문에 성능 이슈가 발생할 수 있습니다.

### 3번 해결방법

userId 별로 쿠폰 발급 개수를 1개로 제한하기만 하면 됩니다. 이럴 때 사용 가능한 Set 자료구조가 있습니다.
Redis에서도 Set 자료구조를 지원하기 때문에 Redis를 사용합니다.

```java
@Repository
@RequiredArgsConstructor
public class AppliedUserRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // 이미 존재하면 0을 반환, 존재하지 않으면 1을 반환
    public Long add(Long userId) {
        return redisTemplate
                .opsForSet()
                .add("applied_user", userId.toString());
    }

}
```

```java
    public void apply(Long userId) {
        Long apply = appliedUserRepository.add(userId);
        if (apply != 1) {
            return;
        }

        Long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }
```