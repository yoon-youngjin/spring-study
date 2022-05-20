# Test Guide

스프링은 다양한 테스트 전략을 제공하고 있다. 대표적으로 Slice Test 라는 것으로 특정 레이어에 대해서 Bean을 최소한으로 등록시켜 테스트 하고자 하는 부분에 최대한 단위 테스트를 지원하다.
다양하게 지원해주는 만큼 테스트 코드를 통일성 있게 관리하는 것이 중요하다.

# 테스트 전략 


| 어노테이션           | 설명                  | 부모 클래스          | Bean         |
| --------------- | ------------------- | --------------- | ------------ |
| @SpringBootTest | 통합 테스트, 전체          | IntegrationTest | Bean 전체      |
| @WebMvcTest     | 단위 테스트, Mvc 테스트     | MockApiTest     | MVC 관련된 Bean |
| @DataJpaTest    | 단위 테스트, Jpa 테스트     | RepositoryTest  | JPA 관련 Bean  |
| None            | 단위 테스트, Service 테스트 | MockTest        | None         |
| None            | POJO, 도메인 테스트       | None            | None         |

# 통합테스트 -> `@SpringBootTest`

## 장점

- 모든 Bean을 올리고 테스트를 진행하기 때문에 쉽게 테스트 진행 가능
- 모든 Bean을 올리고 테스트를 진행하기 때문에 운영환경과 가장 유사하게 테스트 가능
- API를 테스트할 경우 요청부터 응답까지 전체적인 테스트 진행 가능

## 단점

- 모든 Bean을 올리고 테스트를 진행하기 때문에 테스트 시간이 오래 걸림
- 테스트의 단위가 크기 때문에 테스트 실패시 디버깅이 어려움
- 외부 API 콜같은 Rollback 처리가 안되는 테스트 진행을 하기 어려움

## Code

### IntegrationTest

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApp.class)
@AutoConfigureMockMvc
@ActiveProfiles(TestProfile.TEST)
@Transactional
@Ignore
public class IntegrationTest {
    @Autowired protected MockMvc mvc;
    @Autowired protected ObjectMapper objectMapper;
    ...
}
```

- 통합 테스트의 Base 클래스, Base 클래스를 통해서 테스트 전략을 통일성 있게 가져갈 수 있다.
- 통합 테스트는 주로 컨트롤러를 주로 하며 요청부터 응답까지의 전체 플로우를 테스트한다.
- `ActiveProfiles(TestProfile.TEST)` 설정으로 테스트에 profile을 지정한다.
- 인터페이스나 enum 클래스를 통해서 profile을 관리한다. 오타 실수를 줄일 수 있으며 전체적인 프로필이 몇 개 있는지 한 번에 확인할 수 있다.
- `@Transactional` 트랜잭션 어노테이션을 추가하면 테스트코드의 데이터베이스 정보가 자동으로 Rollback 된다. 베이스 클래스에 이 속성을 추가 해야지 실수 없이 진행할 수 있다.
- `@Transactional` 을 추가하면 자연스럽게 데이터베이스 상태 의존적인 테스트를 자연스럽게 하지 않을 수 있게 된다. 
- 통합 테스트 시 필요한 기능들을 `protected`로 제공해줄수 있다. API 테스트를 주로 하게 되니 ObjectMapper 등을 제공해줄 수 있다. 유틸성 메서드들도 `protected` 로 제공해주면 중복 코드 및 테스트 코드의 편의성이 높아 진다. 
- 실제로 동작할 필요가 없으니 `@Ignore` 어노테이션을 추가한다.


---

# 서비스 테스트

## 장점

- 진행하고자 하는 테스트에만 집중할 수 있다.
- 테스트 진행시 중요 관점이 아닌 것들은 Mocking 처리해서 외부 의존성들을 줄일 수 있다.
  - 예를 들어 주문 할인 로직이 제대로 동작하는지에 대한 테스트만 진행하고, 실제로 데이터베이스에 insert되는지는 해당 테스트의 관심이 아니다.
- 테스트 속도가 빠르다.

## 단점

- 의존성 있는 객체를 Mocking 하기 때문에 문제가 완결된 테스트는 아니다.
- Mocking 하기가 귀찮다.
- Mocking 라이브러리에 대한 학습 비용이 발생한다.

## Code

### MockTest

```java
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(TestProfile.TEST)
@Ignore
public class MockTest {

}
```

- 주로 Service 영역을 테스트 한다.
- `MockitoJUnitRunner`을 통해서 Mock 테스트를 진행한다.

### Test Code

```java
public class MemberSignUpServiceTest extends MockTest {

    @InjectMocks
    private MemberSignUpService memberSignUpService;

    @Mock
    private MemberRepository memberRepository;
    private Member member;

    @Before
    public void setUp() throws Exception {
        member = MemberBuilder.build();
    }

    @Test
    public void 회원가입_성공() {
        //given
        final Email email = member.getEmail();
        final Name name = member.getName();
        final SignUpRequest dto = SignUpRequestBuilder.build(email, name);

        given(memberRepository.existsByEmail(any())).willReturn(false);
        given(memberRepository.save(any())).willReturn(member);

        //when
        final Member signUpMember = memberSignUpService.doSignUp(dto);

        //then
        assertThat(signUpMember).isNotNull();
        assertThat(signUpMember.getEmail().getValue()).isEqualTo(member.getEmail().getValue());
        assertThat(signUpMember.getName().getFullName()).isEqualTo(member.getName().getFullName());
    }

    @Test(expected = EmailDuplicateException.class)
    public void 회원가입_이메일중복_경우() {
        //given
        final Email email = member.getEmail();
        final Name name = member.getName();
        final SignUpRequest dto = SignUpRequestBuilder.build(email, name);

        given(memberRepository.existsByEmail(any())).willReturn(true);

        //when
        memberSignUpService.doSignUp(dto);
    }
}
```

- `MockTest` 객체를 상속받아 테스트의 일관성을 갖는다.
- `회원가입_성공` 테스트는 오직 회원 가입에 대한 단위 테스트만 진행한다.
  - `existsByEmail`을 모킹해서 해당 이메일이 중복되지 않았다는 가정을 한다.
  - `then` 에서는 회원 객체가 해당 비즈니스 요구사항에 맞게 생성됐는지를 검사한다.
- `회원가입_이메일중복_경우` 테스트는 회원가입시 이메일이 중복됐는지 여부를 확인한다.
  - `existsByEmail`을 모킹해서 이메일이 중복됐다는 가정을 한다.
  - `expected`으로 이메일이 중복되었을 경우 `EmailDuplicateException` 예외가 발생하는지 확인한다.
  - 해당 이메일이 데이터베이스에 실제로 있어서 예외가 발생하는지는 관심사가 아니다. 작성한 코드가 제대로 동작 여부만이 해당 테스트의 관심사이다.
- 오직 테스트의 관심사만 테스트를 진행하기 때문에 예외 발생시 디버깅 작업도 명확해진다.
- 외부 의존도가 낮기 때문에 테스트 하고자하는 부분만 명확하게 테스트가 가능하다.

## Mock API 테스트

### 장점

- Mock 테스트와 장점은 거의 같다.
- `webApplication` 관련된 Bean들만 등록하기 때문에 통합 테스트보다 빠르게 테스트할 수 있다.
- 통합 테스트를 진행하기 어려운 테스트를 진행
  - 외부 API 같은 Rollback 처리가 힘들거나 불가능한 테스트를 주로 사용한다.
  - 예를 들어 외부 결제 모듈 API를 콜하면 안 되는 케이스에서 주로 사용할 수 있다.
  - 이런 문제는 통합 테스트에서 해당 객체를 Mock 객체로 변경해서 테스트를 진행할 수도 있다.

### 단점

- Mock 테스트와 단점은 거의 같다.


## Code

```java
@WebMvcTest(MemberApi.class)
public class MemberMockApiTest extends MockApiTest {
    @MockBean private MemberSignUpService memberSignUpService;
    @MockBean private MemberHelperService memberHelperService;
    ...

    @Test
    public void 회원가입_유효하지않은_입력값() throws Exception {
        //given
        final Email email = Email.of("asdasd@d"); // 이메일 형식이 유효하지 않음
        final Name name = Name.builder().build();
        final SignUpRequest dto = SignUpRequestBuilder.build(email, name);
        final Member member = MemberBuilder.build();

        given(memberSignUpService.doSignUp(any())).willReturn(member);

        //when
        final ResultActions resultActions = requestSignUp(dto);

        //then
        resultActions
                .andExpect(status().isBadRequest())
        ;

    }
```

- @WebMvcTest(MemberApi.class) 어노테이션을 통해서 하고자 하는 MemberApi의 테스트를 진행한다.
- @MockBean 으로 객체를 주입받아 Mocking 작업을 진행한다.
- 테스트의 관심사는 오직 Request와 그에 따른 Response
