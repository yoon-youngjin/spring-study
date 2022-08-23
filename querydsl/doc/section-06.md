# 실무 활용 - 순수 JPA와 Querydsl (2)

## 스프링 데이터 JPA 레포지토리로 변경

### 스프링 데이터 JPA - MemberRepository 생성

```java
public interface MemberRepository extends JpaRepository<Member, Long>{

    // select m from Member m where m.username = ?
    List<Member> findByUsername(String username);

}
```

### 사용자 정의 레포지토리

Querydsl을 사용하려면 구현코드를 만들어야하는데 스프링 데이터 JPA의 경우 인터페이스로 동작하기 때문에 사용자가 원하는 구현 코드를 넣기 위해서는 사용자 정의 레포지토리를 생성해야한다.


사용자 정의 리포지토리 사용법
1. 사용자 정의 인터페이스 작성
```java
public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition condition);

}

```

2. 사용자 정의 인터페이스 구현

구현체를 작성하는 경우에 `Impl`을 반드시 작성해줘야한다.

```java
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
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
    }

    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username) ? member.username.eq(username) : null;
    }

}
```

3. 스프링 데이터 리포지토리에 사용자 정의 인터페이스 상속

```java
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // select m from Member m where m.username = ?
    List<Member> findByUsername(String username);

}
```

+ 만약 특정한 기능에 맞춰진 조회 기능이며 쿼리가 복잡한 경우라면 별도로 예를 들어 MemberQueryRepository라는 명명으로 구현체를 만들어서 처리한다.
+ 굳이 custom에 억압되어 모든 기능을 넣는 것도 좋은 구현은 아니다.

```java
@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    ...
    
}
```

## 스프링 데이터 페이징 활용1 - Querydsl 페이징 연동

사용자 정의 인터페이스에 페이징 2가지 추가

```java
public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition condition);

    // 단순한 쿼리
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

    // count 쿼리, contents 쿼리
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);


}
```

전체 카운트를 한번에 조회하는 단순한 방법
- searchPageSimple(), fetchResults() 사용

```java

... 

@Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {

        QueryResults<MemberTeamDto> results = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());

    }
    
        ...
```

![image](https://user-images.githubusercontent.com/83503188/185871225-d6853895-1364-43e2-87e4-77242b921862.png)

query가 두번 발생하는 것을 확인할 수 있다.

위 방법은 Deprecated 되었으므로 아래 코드와 같이 데이터 내용과 전체 카운트를 별도로 조회하는 방법을 통해 구현해야 한다.

```java
 @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> contents = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageLoe(condition.getAgeLoe()),
                        ageGoe(condition.getAgeGoe())
                )
        .fetchOne();

        return new PageImpl<>(contents, pageable, totalCount);
    }
```

## 스프링 데이터 페이징 활용2 - CountQuery 최적화

`PageableExecutionUtils.getPage()` 로 최적화

스프링 데이터 라이브러리가 제공

count 쿼리가 생략 가능한 경우 생략해서 처리

1. 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
2. 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함)


위와 같이 1, 2번인 경우에 PageableExecutionUtils.getPage를 사용하면 countquery를 실행하지 않는다.

```java
{
...
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageLoe(condition.getAgeLoe()),
                        ageGoe(condition.getAgeGoe())
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);

        ...
    }
```

![image](https://user-images.githubusercontent.com/83503188/185874025-f6319a4a-e122-4de9-8366-b67b9d594e84.png)
![image](https://user-images.githubusercontent.com/83503188/185874069-57ea683a-1b8d-40f6-81ef-db5d8736ebf6.png)

count 쿼리는 나가지 않음을 확인할 수 있다.

## 스프링 데이터 정렬(Sort)

스프링 데이터 JPA는 자신의 정렬(Sort)을 Querydsl의 정렬(OrderSpecifier)로 편리하게 변경하는 기능을 제공한다. 이 부분은 뒤에 스프링 데이터 JPA가 제공하는 Querydsl 기능에서 살펴보겠다.

스프링 데이터의 정렬을 Querydsl의 정렬로 직접 전환하는 방법은 다음 코드를 참고하자.

```java
JPAQuery<Member> query = queryFactory
        .selectFrom(member);
for (Sort.Order o : pageable.getSort()) {
    PathBuilder pathBuilder = new PathBuilder(member.getType(),
member.getMetadata());
    query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
        pathBuilder.get(o.getProperty())));
}
List<Member> result = query.fetch();
```



참고: 정렬( Sort )은 조건이 조금만 복잡해져도 Pageable 의 Sort 기능을 사용하기 어렵다. 루트 엔티티 범위를 넘어가는 동적 정렬 기능이 필요하면 스프링 데이터 페이징이 제공하는 Sort 를 사용하기 보다는 파라미터를 받아서 직접 처리하는 것을 권장한다.

spring 데이터 sort를 querydsl의 sort로 변경하는 부분이 어렵다. -> 뒤에서 다시 설명