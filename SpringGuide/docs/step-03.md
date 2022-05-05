# Service Guide

## 서비스 레이어란?

![](https://image.slidesharecdn.com/random-151127092631-lva1-app6892/95/-60-638.jpg?cb=1448755823)

- Member라는 객체로 회원가입(객체 생성), 프로필 수정(객체 수정) 모든 행위가 가능하지만 그것을 영속화 시켜야 하기 때문에 별도의 레이어가 필요하고 이것을 서비스 레이어라고 한다. 서비스 레이어에서는 대표적으로 데이터베이스에 대한 트랜잭션을 관리한다.
- 서비스 영역은 도메인의 핵심 비즈니스 코드를 담당하는 영역이 아니라 인프라스트럭처(데이터베이스) 영역과 도메인 영역을 연결해주는 매개체 역할
- **Member 객체에 대한 제어는 Member 스스로 제어해야 한다.**


## 서비스의 적절한 책임의 크기 부여하기
- 책임: 외부 객체의 요청에 대한 응답
- **책임들이 모여 역할이 되고 역할은 대체 가능성을 의미**
- 가능할 정도의 적절한 크기를 가져야 한다.

### 행위 기반으로 네이밍 하기
서비스의 책임의 크기를 잘 부여하는 방법 중에 가장 쉬운 방법, 행위 기반으로 서비스를 만드는 것

- `MemberService` 라는 네이밍은 많이 사용하지만 정말 좋지 않은 패턴, 우선 해당 클래스의 책임이 분명하지 않아서 모든 로직들이 `MemberService` 으로 모이게 된다.
- 그 결과 외부 객체에서는 `MemberService` 객체를 의존
- findById 메서드 하나를 사용하고 싶어도 `MemberService` 전체를 주입받아야 한다.
- `MemberService` 구현도 보인이 모든 구현을 하려고 하니 메서드의 라인 수도 방대해짐
- Member에 대한 조회 전용 서비스 객체인 `MemberFindService` 으로 네이밍을 하면 자연스럽게 객체의 책임이 부여

### 역할은 대체 가능성을 의미
- 메서드(책임)란 것은 외부 객체의 호출에 대한 응답이고, 이러한 메서드(책임)들이 모여 클래스(역할)가 되고 클래스(역할)는 인터페이스(대체 가능성)을 의미

#### 책임의 크기가 적절해야하는 이유

```java
public interface MemberService {

    Member findById(MemberId id);

    Member findByEmail(Email email);

    void changePassword(PasswordDto.ChangeRequest dto);

    Member updateName(MemberId id, Name name);
}
```

위 같은 Service, ServiceImpl 구조는 스프링 예제에서 많이 사용되는 예제, 위 객체의 책임은 크게 member 조회, 수정이다. 이 책임이 모여 클래스가 된다. 이 클래스(역할)는 대체 가능성을 의미, **위의 인터페이스는 대체 불가능**

findById, findByEmail, changePassword, updateName의 세부 구현이 모두 다른 구현체가 있기 힘들다. 이렇듯 객체의 책임이 너무 많으면 대체성을 갖지 못하게 된다.

### 서비스의 적절한 크기는 대체 가능성을 염두 하는 것

- 우선 행위 기반으로 서비스의 네이밍을 하면 자연스럽게 해당 행위에 대해서 책임이 할당
- 행위 기반으로 책임을 할당하면 자연스럽게 대체 가능성을 갖게 될 수 있다.

```java
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReferralCode {

    @Column(name = "referral_code", length = 50)
    private String value;

    private ReferralCode(String value) {
        this.value = value;
    }

    public static ReferralCode of(final String value) {
        return new ReferralCode(value);
    }

    public static ReferralCode generateCode() {
        return new ReferralCode(RandomString.make(10));
    }
}

@Service
@Transactional
@RequiredArgsConstructor
public class MemberSignUpService { // (1)

    private final MemberRepository memberRepository;

    public Member doSignUp(final SignUpRequest dto) {

        if (memberRepository.existsByEmail(dto.getEmail())) { //(2)
            throw new EmailDuplicateException(dto.getEmail());
        }

        final ReferralCode referralCode = generateUniqueReferralCode();
        return memberRepository.save(dto.toEntity(referralCode));
    }

    private ReferralCode generateUniqueReferralCode() { //(3)
        ReferralCode referralCode;
        do {
            referralCode = ReferralCode.generateCode(); //(4)
        } while (memberRepository.existsByReferralCode(referralCode)); // (5)

        return referralCode;
    }

}
```

1. MemberSignUpService 네이밍을 통해서 행위 기반의 책임을 부여
2. Email의 존재 여부는 데이터베이스에 있음으로 존재 여부는 memberRepository를 사용
3. 유니크한 referralCode를 생성을 위한 메서드
4. **ReferralCode에 대한 생성은 ReferralCode 객체과 관리**
5. 해당 코드가 존재하는지는 데이터베이스에 있음으로 존재 여부는 memberRepository를 사용

ReferralCode에 대한 생성 비즈니스 로직은 ReferralCode 객체가 스스로 제어하고 있다. 이것이 데이터베이스에 중복 여부 검사를 서비스 레이어에서 진행

