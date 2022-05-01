# Domain Guide 

- 도메인 객체는 우리가 해결하고자 하는 핵심 비즈니스 로직이 반영되는 곳
- 특히 도메인 객체에서 자기 자신의 책임을 충분히 다하지 않으면 그 로직들은 자연스럽게 Service 영역 및 외부 영역에서 해당 책임을 넘겨받아 구현하게 된다.

## Member 클래스

```java
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"email", "name"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true, updatable = false, length = 50))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "referral_code", nullable = false, unique = true, updatable = false, length = 50))
    private ReferralCode referralCode;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "first", column = @Column(name = "first_name", nullable = false)),
            @AttributeOverride(name = "middle", column = @Column(name = "middle_name")),
            @AttributeOverride(name = "last", column = @Column(name = "last_name", nullable = false))
    })
    private Name name;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Builder
    public Member(Email email, ReferralCode referralCode, Name name) {
        this.email = email;
        this.referralCode = referralCode;
        this.name = name;
    }

    public void updateProfile(final Name name) {
        this.name = name;
    }
}

```

## 실무에서 Lombok 사용법 요약

- `@NoArgsContructor(access = AccessLevel.PROTECTED)` JPA에서는 프록시 객체가 필요하므로 기본 생성자 하나가 반드시 있어야 한다. 이때 접근지시자는 낮은 접근지시자인 `protected`를 사용하자.
- `@Data`는 사용하지 말자, 너무 많은 것들을 해준다.
- `@Setter`는 사용하지 말자, 객체는 변경 포인트를 남발하지 말자.
- `@ToString`을 사용하면 무한 참조가 생길 수 있다 -> `@ToString(of={""}` 권장
- 클래스 상단의 `@Builder` X, 생성자 위에 `@Builder` OK

Lombok이 자동으로 해주는 것들을 남용하다 보면 코드의 안정성이 낮아진다. 

## JPA 어노테이션

- `@Table(name= "member")`: 테이블 네임은 반드시 명시, 명시하지 않으면 기본적으로 클래스 네임을 참조하기 때문에 클래스 네임 변경 시 영향을 받게 된다.
- `@Column`: 컬럼 네임도 클래스 네임과 마찬가지로 반드시 지정
- `nullable`, `unique`, `updatable` 등의 기능을 적극 활용, 이메일의 경우 `nullable`, `unique` 같은 속성을 반드시 추가
- `@CreationTimestamp`, `@UpdateTimestamp` 어노테이션을 이용하여 생성, 수정 시간을 쉽게 설정할 수 있습니다.

## Embedded 적극 활용하기 
- `Embedded` 어노테이션을 이용하여 도메인 객체의 책임을 나눌 수 있다. **앞에서 언급했듯이, 객체가 자기 자신의 책임을 다하지 않으면 그 책임은 자연스럽게 다른 객체에게 넘어가게 된다.**
- `Name`, `Address` 객체들이 대표적인 `Embedded` 대상이 되는 객체들입니다. `Member` 객체에서 `Embedded`으로 해당 객체를 가지고 있지 않았다면 다음과 같이 작성

```java
class Member {
    @NotEmpty 
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @NotEmpty @Column(name = "last_name", length = 50)
    private String lastName;

    @NotEmpty @Column(name = "county")
    private String county;

    @NotEmpty
    @Column(name = "state")
    private String state;

    @NotEmpty
    @Column(name = "city")
    private String city;

    @NotEmpty
    @Column(name = "zip_code")
    private String zipCode;
}
```

- 전체 이름, 전체 주소를 가져오기 위해서는 Member 객체에서 기능을 구현해야 한다.
- 즉 Member의 책임이 늘어나는 것
- 그뿐만이 아니라, `Name`, `Address`는 많은 도메인 객체에서 사용되는 객체이므로 중복 코드의 증가를 유발
```java
public class Name {
    @NotEmpty @Column(name = "first_name", length = 50)
    private String first;

    @Column(name = "middle_name", length = 50)
    private String middle;

    @NotEmpty @Column(name = "last_name", length = 50)
    private String last;
}

public class Address {
    @NotEmpty @Column(name = "county")
    private String county;

    @NotEmpty @Column(name = "state")
    private String state;

    @NotEmpty @Column(name = "city")
    private String city;

    @NotEmpty @Column(name = "zip_code")
    private String zipCode;
}

public class Member {
    @Embedded private Name name;
    @Embedded private Address address;
}
```
- 만약 주문이라는 객체가 있다면 `Name`, `Address` 객체를 그대로 사용할 수 있다.
- `Embedded`의 장점 정리
1. 데이터 응집력 증가
2. 중복 코드 방지
3. 책임의 분산
4. 테스트 코드 작성의 용이함

## Rich Object

- 객체지향에서 가장 기본적이며 핵심적인 것
- JPA도 객체지향 프로그래밍을 돕는(패러다임 불일치를 해결) 도구
- 도메인 객체들에 기본적으로 getter, setter 외에는 메서드를 작성하지 않는 경우가 있다. 이렇게 되면 객체 보인의 책임을 다하지 않으니 이런 책임들이 다른 객체에서 이루어지게 된다.
- 다음은 쿠폰 도메인 객체 코드
```java
public class Coupon {

    @Embedded
    private CouponCode code;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "discount", nullable = false)
    private double discount;

    @Column(name = "expiration_date", nullable = false, updatable = false)
    private LocalDate expirationDate;

    public boolean isExpiration() {
        return LocalDate.now().isAfter(expirationDate);
    }

    public void use() {
        verifyExpiration();
        verifyUsed();
        this.used = true;
    }

    private void verifyUsed() {
        if (used) throw new CouponAlreadyUseException();
    }

    private void verifyExpiration() {
        if (LocalDate.now().isAfter(getExpirationDate())) throw new CouponExpireException();
    }
}
```


- 쿠폰에 만료 여부, 쿠폰의 사용 가능 여부, 쿠폰의 사용 등의 메서드는 당연히 쿠폰 객체 자신에서 제공해야 한다.

> 출처 : 객체지향의 사실과 오해 (정말 정말 추천드리고 싶은 도서입니다.)
>
> 객체는 충분히 '협력적'이어야 한다. 객체는 다른 객체의 요청에 충실히 귀 기울이고 다른 객체에게 적극적으로 도움을 요청할 정도로 열린 마음을 지녀야 한다. 객체는 다른 객체의 명령에 복종하는 것이 아니라 요청에 응답할 뿐이다. 어떤 방식으로 응답할지는 객체 스스로 판단하고 결장한다. 심지어 요청에 응할지 여부도 객체 스스로 결정할 수 있다.


- 단순하게 getter, setter 메서드만 제공한다면 이는 협력적인 관계가 아닌 복종하는 관계에 지나지 않는다. 
- 요청에 응답할지 자체도 객체 스스로가 결정할 수 있게 객체의 자율성을 보장해야 한다.
- `use()` 메서드 요청이 오더라도 쿠폰 객체는 해당 요청이 알맞지 않다고 판다하면 그 요청을 무시하고 예외를 발생시킨다. -> 이렇듯 객체의 자율성이 있어야 한다.

- setter를 사용하게 되면 해당 객체는 복종하는(수동적인) 관계를 갖게 된다.
- `setUse(true)` 메서드는 그저 used 필드를 true 변경하는 외부 객체에 복종하는 메서드일 뿐
- 쿠폰 객체 스스로가 자율성을 갖고 해당 메시지에 응답을 할지의 여부도 판단해야 외부 객체와 능동적인 관계를 갖게 된다. 
- 또한 복종하는 관계일 경우 쿠폰 사용 로직을 만들기 위해서 내가 객체의 세부적인 사항을 모두 알고 있어야 한다. -> 쿠폰 만료일, 만료 여부, 기타 등등 수많은 세부사항을 다 알고 검사를 하고 나서 비로소 `use()` 메서드를 호출

---

## Dao와 Repository의 차이점

- DAO는 Data Peristence의 추상화이고, Repository는 객체 Collection의 추상화이다.
- DAO는 storage system에 더 가까운 개념이고 상대적으로 low level concept, Repository는 Domain객체에 가까운 개념이며 상대적으로 high level concept
- DAO는 데이터 맵핑/접근 계층으로 쿼리를 숨기지만, Repository는 Domain과 DAL사이의 계층으로 데이터를 대조하고 Domain 객체로 Mapping하는 로직을 숨긴다.
- DAO는 Repository를 사용하여 구현할 수 없지만, Repository는 DAO를 사용해 구현할 수 있다.

Data Access한다는 점에서 Repository와 DAO는 공통점을 갖지만, Repository는 객체 중심, DAO는 데이터 저장소(DB 테이블) 중심인 것이다.

또한, Repository는 객체 중심으로 데이터를 다루기 위해 하나 이상의 DAO를 사용할 수 있으며, 따라서 DAO보다 higher layer이다.
