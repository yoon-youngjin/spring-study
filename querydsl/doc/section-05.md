# 실무 활용 - 순수 JPA와 Querydsl (1)

## 순수 JPA 레포지토리와 Querydsl

### 순수 JPA 레포지토리
```java
@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);

    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createQuery("select m from Member m where m.username =:username", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    
}
```

### Querydsl 사용

- `findAll()`

```java
public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

public List<Member> findAll_Querydsl() {
        return queryFactory
        .selectFrom(member)
        .fetch();
        }
```

- `findByUsername()`

```java
public List<Member> findByUsername(String username) {
        return em.createQuery("select m from Member m where m.username =:username", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findByUsername_Querydsl(String username) {
        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }
```

+ JPAQueryFactory 스프링 빈 등록

JPAQueryFactory는 생성자를 통해 주입받아도 되고 Bean으로 등록해도 된다.

```java
@SpringBootApplication
public class QuerydslApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuerydslApplication.class, args);
    }

    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
```

```java
@Repository
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory; 

    public MemberJpaRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    }
...
```

빈으로 주입받는 방식을 사용하면 테스트 코드를 작성할 때 불편하다는 단점이 있지만 아래와 같이 `@RequiredArgsContrurctor`를 통해 코드를 깔끔하게 처리할 수 있다는 장점이 있다.

```java
@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    ...
}
```


참고: 동시성 문제는 걱정하지 않아도 된다. 왜냐하면 여기서 스프링이 주입해주는 엔티티 매니저는 실제 동작 시점에 진짜 엔티티 매니저를 찾아주는 프록시용 가짜 엔티티 매니저이다. 이 가짜 엔티티 매니저는 실제 사용 시점에 트랜잭션 단위로 실제 엔티티 매니저(영속성 컨텍스트)를 할당해준다.

## 동적 쿼리와 성능 최적화 조회 - Builder 사용

MemberTeamDto - 조회 최적화용 DTO 추가

```java
@Data
public class MemberTeamDto {

    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }

}
```

참고: @QueryProjection 을 사용하면 해당 DTO가 Querydsl을 의존하게 된다. 이런 의존이 싫으면, 해당 에노테이션을 제거하고, Projection.bean(), fields(), constructor() 을 사용하면 된다.

```java
@Data
public class MemberSearchCondition {
    // 조건: 회원명, 팀명, 나이(ageGoe, ageLoe)

    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;

}

```

### 동적쿼리 - Builder 사용

```java
public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(condition.getUsername())) {
            builder.and(member.username.eq(condition.getUsername()));
        }
        if (StringUtils.hasText(condition.getTeamName())) {
            builder.and(team.name.eq(condition.getTeamName()));
        }
        if (condition.getAgeGoe() != null) {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }
        if (condition.getAgeLoe() != null) {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }

        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();
    }
```

문자열의 입력은 "" 또는 null이 들어올 수 있다. SpringFramework의 StringUtils.hasText()를 통해 처리하자.

Builder를 사용하여 동적쿼리를 처리하게 된다면 위와 같이 코드가 지저분해지고 있다.

조건이 하나도 존재 없다면?

![image](https://user-images.githubusercontent.com/83503188/185780337-bb1adb4a-1106-4e5e-864a-99ee2e838f05.png)

모든 데이터를 검색하는 select query가 발생한다.

따라서 동적쿼리의 경우 기본값이 존재하는 것이 좋다.

또한 가급적이면 페이징 쿼리가 같이 존재하는 것이 좋다

### Where절에 파라미터를 사용한 예제

```java
public List<MemberTeamDto> search(MemberSearchCondition condition) {

        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageLoe(condition.getAgeLoe()),
                        ageGoe(condition.getAgeGoe())
                )
                .fetch();
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        if (ageGoe != null) {
            return member.age.goe(ageGoe);
        }
        return null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        if (ageLoe != null) {
            return member.age.loe(ageLoe);
        }
        return null;
    }\

    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username) ? member.username.eq(username) : null;
    }

```

where절을 사용하여 동적쿼리를 처리하게 된다면 함수로 구현한 조건함수를 조립하여 재사용할 수 있다는 장점이 있다.

```java
private BooleanExpression ageBetween(int ageLoe, int ageGoe) {
        return ageGoe(ageGoe).and(ageLoe(ageLoe));
    }
```

## 조회 API 컨트롤러 개발

편리한 데이터 확인을 위해 샘플 데이터를 추가하자.

샘플 데이터 추가가 테스트 케이스 실행에 영향을 주지 않도록 다음과 같이 프로파일을 설정하자.

테스트를 실행할 때랑 로컬로 톰캣을 띄울때를 서로 다른 상황으로 profile을 실행한다.

프로파일 설정
`src/main/resources/application.yml`

```java

```yaml
spring:
  profiles:
    active: local
...

```

`src/test/resources/application.yml`

```java
```yaml
spring:
  profiles:
    active: test
...
```

초기 데이터 set

```java
@Profile("local") // local환경에서만 실행
@Component //  Componet Scan을 통해 자동으로 Spring Bean으로 등록하게 해준다.
@RequiredArgsConstructor
public class IntiMember {

    private final InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, selectedTeam));
            }
        }
    }

    private final MemberJpaRepository memberJpaRepository;

}
```


init() 내용을 @PostConstruct에 넣으면 안되나?
- Spring lifecycle에 의해서 `@PostConstruct`와 `@Transactional` 두 가지 어노테이션을 동시에 사용할 수 없다.




