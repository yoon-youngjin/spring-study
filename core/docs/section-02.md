# 스프링 핵심 원리 이해1 - 예제 만들기

## 비즈니스 요구사항과 설계
- 회원
  - 회원을 가입하고 조회할 수 있다.
  - 회원은 일반과 VIP 두 가지 등급이 있다.
  - 회원 데이터는 자체 DB를 구축할 수 있고, 외부 시스템과 연동할 수 있다. (미확정)
- 주문과 할인 정책
  - 회원은 상품을 주문할 수 있다.
  - 회원 등급에 따라 할인 정책을 적용할 수 있다.
  - 할인 정책은 모든 VIP는 1000원을 할인해주는 고정 금액 할인을 적용해달라. (나중에 변경 될 수 있다.)
  - 할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고 싶다. 최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)

요구사항을 보면 회원 데이터, 할인 정책 같은 부분은 지금 결정하기 어려운 부분이다. 그렇다고 이런 정책이 결정될 때 까지 개발을 무기한 기다릴 수 도 없다.

우리는 앞에서 배운 객체 지향 설계 방법을 이용하자. 

인터페이스를 만들고 구현체를 언제든지 갈아끼울 수 있도록 설계하면 된다. 

### 회원 도메인 설계


**회원 도메인 협력 관계**

![image](https://user-images.githubusercontent.com/83503188/190847622-dfc22aab-c4c4-41b7-b2e5-724f54943d7f.png)

- 회원 서비스는 2가지 기능(회원가입, 회원조회)을 제공
- 회원 데이터는 자체 DB를 구축할 수도 있고 외부 시스템과 연동할 수도 있으므로 회원데이터에 접근하는 계층을 따로 만들어야 한다. -> 회원 저장소라는 인터페이스 생성
- 아직 미확정이므로 메모리 회원 저장소를 통해 테스트

**회원 클래스 다이어그램**

![image](https://user-images.githubusercontent.com/83503188/190847725-25f5e6c4-5652-4a52-90fc-5429a8360ebb.png)

**회원 객체 다이어그램**

![image](https://user-images.githubusercontent.com/83503188/190847759-d9f2a591-f35f-4309-945c-952102949b81.png)

### 회원 도메인 개발

#### 회원 엔티티

**회원 등급**

```java
public enum Grade {
    BASIC,
    VIP
}

```

**회원 엔티티**

```java
@AllArgsConstructor
@Getter
@Setter
public class Member {

    private Long id;
    private String name;
    private Grade grade;

}
```

#### 회원 저장소

**회원 저장소 인터페이스**
```java
public interface MemberRepository {

    void save(Member member);

    Member findById(Long memberId);

}
```

**메모리 회원 저장소 구현체**

```java
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();

    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }

}
```

데이터베이스가 아직 확정이 안되었다. 그래도 개발은 진행해야 하니 가장 단순한, 메모리 회원 저장소를 구현해서 우선 개발을 진행하자.

> 참고: HashMap 은 동시성 이슈가 발생할 수 있다. 이런 경우 ConcurrentHashMap 을 사용하자.

#### 회원서비스


**회원 서비스 인터페이스**

```java
public interface MemberService {

    void join(Member member);

    Member findMember(Long memberId);
}
```

**회원 서비스 구현체**

```java
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository = new MemoryMemberRepository();

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
```


#### Test


```java
public class MemberServiceTest {

    private final MemberService memberService = new MemberServiceImpl();
    
    @Test
    public void join() throws Exception {
        // given
        Member member = new Member(1L, "yoon", Grade.VIP);

        // when
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        // then
        Assertions.assertThat(member).isEqualTo(findMember);

    }
   

}
```

```java
private final MemberService memberService = new MemberServiceImpl();

```

의존관계가 인터페이스 뿐만 아니라 구현체까지 모두 의존하는 문제점이 존재한다.

### 주문과 할인 도메인 설계

**주문 도메인 협력, 역할, 책임**

![image](https://user-images.githubusercontent.com/83503188/190848709-531283f4-1e0a-48b4-8a9f-c46f2f6c646e.png)

1. 주문 생성: 클라이언트는 주문 서비스에 주문 생성을 요청한다.
2. 회원 조회: 할인을 위해서는 회원 등급이 필요하다. 그래서 주문 서비스는 회원 저장소에서 회원을 조회한다.
3. 할인 적용: 주문 서비스는 회원 등급에 따른 할인 여부를 할인 정책에 위임한다.
4. 주문 결과 반환: 주문 서비스는 할인 결과를 포함한 주문 결과를 반환한다.


**주문 도메인 전체**

![image](https://user-images.githubusercontent.com/83503188/190848751-b8858383-d63b-4d1f-9fb1-64f669ddcbf7.png)

역할과 구현을 분리해서 자유롭게 구현 객체를 조립할 수 있게 설계했다. 덕분에 회원 저장소는 물론이고, 할인 정책도 유연하게 변경할 수 있다.

**주문 도메인 클래스 다이어그램**

![image](https://user-images.githubusercontent.com/83503188/190848768-47b865e6-df72-4770-83c9-8913890fc268.png)

보통 인터페이스에 대한 구현체가 딱 하나인 경우에 구현체 명을 인터페이스명 + 'impl'로 한다.

**주문 도메인 객체 다이어그램1**

![image](https://user-images.githubusercontent.com/83503188/190848783-3a438c0b-a6ff-4b03-b404-8b5a34f725a2.png)

회원을 메모리에서 조회하고, 정액 할인 정책(고정 금액)을 지원해도 주문 서비스를 변경하지 않아도 된다. 역할들의 협력 관계를 그대로 재사용 할 수 있다.

**주문 도메인 객체 다이어그램2**

![image](https://user-images.githubusercontent.com/83503188/190848789-1e9e7a1f-cf60-4001-9074-8bd4931c236d.png)

회원을 메모리가 아닌 실제 DB에서 조회하고, 정률 할인 정책(주문 금액에 따라 % 할인)을 지원해도 주문 서비스를 변경하지 않아도 된다.
협력 관계를 그대로 재사용 할 수 있다.

### 주문과 할인 도메인 개발

**할인 정책 인터페이스**

```java
public interface DiscountPolicy {

    /**
     * @return 할인 대상 금액
     */
    int discount(Member member, int price);
}

```

**정액 할인 정책 구현체**

```java
public class FixDiscountPolicy implements DiscountPolicy{

    private int discountFixAmount = 1000; // 1000원 할인

    @Override
    public int discount(Member member, int price) {

        if (member.getGrade() == Grade.VIP) {
            return discountFixAmount;
        } else {
            return 0;
        }
    }
}
```

**주문 엔티티**

```java
@AllArgsConstructor
@Getter @Setter
@ToString
public class Order {

    private Long memberId;
    private String itemName;
    private int itemPrice;
    private int discountPrice;

    // 비즈니스 계산로직
    public int calculatePrice() {
        return itemPrice - discountPrice;
    }


}
```

**주문 서비스 인터페이스**

```java
public interface OrderService {
    
    Order createOrder(Long memberId, String itemName, int itemPrice);
    
}
```

**주문 서비스 구현체**

```java
public class OrderServiceImpl implements OrderService {

    MemberRepository memberRepository = new MemoryMemberRepository();
    DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {


        Member findMember = memberRepository.findById(memberId);
        int discount = discountPolicy.discount(findMember, itemPrice);
        Order order = new Order(memberId, itemName, itemPrice, discount);

        return order;
    }
}
```

주문 생성 요청이 오면, 회원 정보를 조회하고, 할인 정책을 적용한 다음 주문 객체를 생성해서 반환한다. 메모리 회원 리포지토리와, 고정 금액 할인 정책을 구현체로 생성한다.

현재 설계가 잘 된 이유는 `OrderService` 입장에서는 할인에 관해서는 모름 결과만 받아서 사용할 뿐 -> 단일 책임 원칙

할인정책이 변경되어도 `OrderService` 는 변경되지 않는다.

#### Test


```java
public class OrderServiceTest {


    private final MemberService memberService = new MemberServiceImpl();
    private final OrderService orderService = new OrderServiceImpl();

    @Test
    public void createOrder() throws Exception {
        // given
        Long memberId = 1L;
        Member member = new Member(memberId, "yoon", Grade.VIP);
        memberService.join(member);

        // when
        Order order = orderService.createOrder(memberId, "itemA", 10000);

        // then
        assertThat(order.getDiscountPrice()).isEqualTo(1000);
      
    }
}

```