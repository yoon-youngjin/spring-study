# 기본 문법

## JPQL vs Querydsl

```java
 @Test
public void startJPQL() {
        // member1을 찾아라.
        String qlString = "select m from Member m where m.username =:username";
        Member findByJPQL = em.createQuery(qlString, Member.class)
        .setParameter("username", "member1")
        .getSingleResult();

        assertThat(findByJPQL.getUsername()).isEqualTo("member1");
        }
```

```java
@Test
    public void startQuerydsl() {

        Member findByQuerydsl = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) // 위의 setParameter 와 같은 파라미터 바인딩 처리
                .fetchOne();

        assertThat(findByQuerydsl.getUsername()).isEqualTo("member1");

    }
```

JPQL에서 Query String 은 문자열로 작성되어 실행시점에 오류를 잡을 수 있다. 따라서 사용자가 해당 쿼리를 실행하는 런타임에 오류가 나게되는 최악의 경우가 발생한다.<br>
단, 인텔리제이 엔터프라이즈 버전을 사용 중인 경우에는 어느정도의 오류를 알려준다. <br>
또한, JPQL은 파라미터 바인딩을 직접(`.setParameter("username", "member1")`)하는데 반해 Querydsl은 파라미터 바인딩을 자동으로 처리해준다.

## 기본 Q-Type 활용

### Q클래스 인스턴스를 사용하는 2가지 방법

```java
QMember qMember = new QMember("m"); //별칭 직접 지정
QMember qMember = QMember.member; //기본 인스턴스 사용
```

### 기본 인스턴스를 static import와 함께 사용

```java
import static study.querydsl.entity.QMember.*;
```

+ 다음 설정을 추가하면 실행되는 JPQL을 볼 수 있다.

```yaml
spring.jpa.properties.hibernate.use_sql_comments: true  
```

![image](https://user-images.githubusercontent.com/83503188/185735428-2f3f0454-6be8-47ad-b623-14916bdad4fc.png)


위 사진에서 "member1"(=alias)인 이유는 

![image](https://user-images.githubusercontent.com/83503188/185735448-e664b5e0-4691-43ab-8bb6-1a10a453ba09.png)

`new QMember("")`를 통해 새로운 alias로 생성해주는 것이 아닌 QType에 내장된 static 필드를 통해 QType를 생성하게 되면 기본적으로 동일한 alias를 사용하기 때문이다. <br>
따라서 같은 테이블을 조인해야하는 경우에는 QType에 내장된 static 필드를 사용하는 것이 아닌 직접 생성하여 alias를 변경해줘야 한다.

## 검색 조건 쿼리

```java
@Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
```

검색 조건은 .and() , .or() 를 메서드 체인으로 연결할 수 있다.

### JPQL이 제공하는 모든 검색 조건 제공

```java
member.username.eq("member1") // username = 'member1'
member.username.ne("member1") //username != 'member1'
member.username.eq("member1").not() // username != 'member1'
        
member.username.isNotNull() //이름이 is not null
        
member.age.in(10, 20) // age in (10,20)
member.age.notIn(10, 20) // age not in (10, 20)
member.age.between(10,30) //between 10, 30
        
member.age.goe(30) // age >= 30
member.age.gt(30) // age > 30
member.age.loe(30) // age <= 30
member.age.lt(30) // age < 30
        
member.username.like("member%") //like 검색
member.username.contains("member") // like ‘%member%’ 검색
member.username.startsWith("member") //like ‘member%’ 검색
...
```

## 결과 조회

- fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
- fetchOne() : 단 건 조회
  - 결과가 없으면 : null
  - 결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
- fetchFirst() : limit(1).fetchOne()
- fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행 -> Deprecated
- fetchCount() : count 쿼리로 변경해서 count 수 조회 -> Deprecated

```java
@Test
    public void resultFetch() {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        Member fetchOne = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst(); // == .limit(1).fetchOne()

        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();
        
        results.getTotal();
        results.getLimit();
        results.getOffset();
        List<Member> content = results.getResults();
        
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();

        long totalCount = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        System.out.println("totalCount = " + totalCount);
    }
```

- `fetchResults()`
  - fetchResult는 getTotal, getLimit, getOffset을 제공 -> count 쿼리를 가져오기 위해 2개의 쿼리 발생
  - fetchResult는 paging Query가 복잡해지면 contents를 가져오는 쿼리와 count 쿼리가 달라지는 문제가 발생할 수 있다.(성능 이슈) -> 복잡하고 성능이 중요한 페이징 처리의 경우 따로 쿼리를 날려줘야한다.
  - fetchResult는 Deprecated 되었으므로 fetch를 통해 content를 조회하고
  - count Query를 통해 total count를 다시 조회해야 한다.

- `fetchCount()`
  - fetchCount()는 개발자가 작성한 select 쿼리를 기반으로 count용 쿼리를 내부에서 만들어서 실행한다.
  - 따라서 단순한 쿼리에서는 잘 동작하지만, 복잡한 쿼리에서는 제대로 동작하지 않는다.

```java

@Test
public void count() {
    Long totalCount = queryFactory
    //.select(Wildcard.count) //select count(*)
    .select(member.count()) //select count(member.id)
    .from(member)
    .fetchOne();
    System.out.println("totalCount = " + totalCount);
}
```
- count(*) 을 사용하고 싶으면 예제의 주석처럼 Wildcard.count 를 사용하면된다.
- member.count() 를 사용하면 count(member.id) 로 처리된다.
- 응답 결과는 숫자 하나이므로 fetchOne() 을 사용한다.

## 정렬

- 회원 정렬 순서
  1. 회원 나이 내림차순(desc)
  2. 회원 이름 오름차순(asc)
  3. 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)

```java
@Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(
                        member.age.desc(),
                        member.username.asc().nullsLast()
                )
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();


    }
```


- desc() , asc() : 일반 정렬
- nullsLast() , nullsFirst() : null 데이터 순서 부여

## 페이징 

### 조회 건수 제한

```java
 @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc()) // member4, member3, member2, member1
                .offset(1) // 앞에서 몇개를 skip? -> 0부터 시작이므로 1이면 1개를 skip -> member3, member2, member1
                .limit(2) // member3, member2
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

```

### 전체 조회 수가 필요하면?

```java
@Test
public void paging2() {
        QueryResults<Member> fetchResults = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc()) // member4, member3, member2, member1
        .offset(1) // 앞에서 몇개를 skip? -> 0부터 시작이므로 1이면 1개를 skip -> member3, member2, member1
        .limit(2) // member3, member2
        .fetchResults();

        // 위 코드는 Deprecated 아래와 같이 나눠서 작성하자.
        List<Member> result = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc()) // member4, member3, member2, member1
        .offset(1) // 앞에서 몇개를 skip? -> 0부터 시작이므로 1이면 1개를 skip -> member3, member2, member1
        .limit(2) // member3, member2
        .fetch();

        long count = queryFactory
        .select(member.count())
        .from(member)
        .fetchOne();

        assertThat(fetchResults.getTotal()).isEqualTo(4);
        assertThat(fetchResults.getResults().size()).isEqualTo(2);

        assertThat(count).isEqualTo(4);
        assertThat(result.size()).isEqualTo(2);
        }
```

## 집합

### 집합 함수
```java
@Test
    public void aggregation() {
        Tuple tuple = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetchOne();

        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);

    }
```

- JPQL이 제공하는 모든 집합 함수를 제공한다.
- tuple은 프로젝션과 결과반환에서 설명한다.
- 데이터 타입이 여러개인 경우 Querydsl은 Tuple 타입으로 제공한다.
- 실무에서는 Tuple을 잘 사용하지 않고 Dto를 통해 직접 조회하는 방법을 사용한다.

### GroupBy 사용

팀의 이름과 각 팀의 평균 연령을 구해라.

```java
@Test
    public void group() throws Exception {

        List<Tuple> result = queryFactory
                .select(
                        team.name,
                        member.age.avg()
                )
                .from(member)
                .join(member.team, team) // member에 있는 team을 join 해준다.
                .groupBy(team.name) // team의 이름으로 grouping
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }
```

![image](https://user-images.githubusercontent.com/83503188/185736040-f5c767b3-b283-4cd4-b644-ce2a30357cc1.png)

groupBy , 그룹화된 결과를 제한하려면 having

### groupBy(), having() 예시

```java
…
.groupBy(item.price) // 아이템의 가격으로 grouping
.having(item.price.gt(1000)) // having을 통해 grouping된 결과에서 1000원 이상인 아이템만 제한
…
```

## 조인 - 기본 조인

### 기본 조인

조인의 기본 문법은 첫 번째 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭(alias)으로 사용할 Q 타입을 지정하면 된다.

```text
join(조인 대상, 별칭으로 사용할 Q타입)
```

팀 A에 소속된 모든 회원

```java
@Test
    public void join() throws Exception {
        List<Member> results = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(results)
                .extracting("username")
                .containsExactly("member1", "member2");
    }
```

- join() , innerJoin() : 내부 조인(inner join)
- leftJoin() : left 외부 조인(left outer join)
- rightJoin() : rigth 외부 조인(rigth outer join)
- JPQL의 on 과 성능 최적화를 위한 fetch 조인 제공 다음 on 절에서 설명

> left join?
> 
> https://velog.io/@haerong22/LEFT-OUTER-JOIN-%EC%9D%98-%ED%95%A8%EC%A0%95

```java
@Test
    public void leftJoin() throws Exception {
        List<Tuple> results = queryFactory
                .select(
                        member.id,
                        member.username,
                        team.name
                )
                .from(member)
                .leftJoin(team).on(member.id.eq(team.id))
                .fetch();

        for (Tuple tuple : results) {
            System.out.println(tuple.get(team.name));
        }
    }
```

![image](https://user-images.githubusercontent.com/83503188/185736151-3b1634d8-cba7-4db0-ba74-61b65231d9b5.png)

위의 `leftjoin()`을 `join()`으로 바꾸게 되면 on절에 조건에 부합하지 않는 데이터는 가져오지 않는다.

### 세타 조인

- 연관관계가 없는 필드로 조인

회원의 이름이 팀 이름과 같은 회원 조회

```java
@Test
    public void theta_join() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Member> results = queryFactory
                .select(member)
                .from(member, team) // 기존: member.team, team -> member, team -> member와 team을 모두 가져온 후 where절을 통해 필터링하는 방법
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(results)
                .extracting("username")
                .containsExactly("teamA", "teamB");

    }
```

![image](https://user-images.githubusercontent.com/83503188/185736606-c0da6443-21da-4825-9411-8eef94097109.png)


- from 절에 여러 엔티티를 선택해서 세타 조인
- 외부 조인 불가능 다음에 설명할 조인 on을 사용하면 외부 조인 가능

### 조인 - on절

ON절을 활용한 조인(JPA 2.1부터 지원)
   1. 조인 대상 필터링
   2. 연관관계 없는 엔티티 외부 조인


조인 대상 필터링

- 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
- JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
```java
@Test
    public void join_on_filtering() throws Exception {

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                // left join이기 때문에 member를 기준으로 모든 member 데이터는 가져온다.
                // 기본적으로 member의 아이디를 기준으로 테이블을 생성
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
//            System.out.println(tuple.get(member));
//            System.out.println(tuple.get(team));
            System.out.println(tuple);
        }
    }
```

![image](https://user-images.githubusercontent.com/83503188/185736646-37d3c1a1-9616-45f0-8a02-5a40f3cb1e42.png)

![image](https://user-images.githubusercontent.com/83503188/185736652-3721fb78-4789-4df0-9531-1af0fdc51482.png)

left join을 join으로 바꾸게 되면? 
- join 대상에 없는(=teamB) 것들은 사라지게 된다.

![image](https://user-images.githubusercontent.com/83503188/185736705-a74d720c-79ba-4dee-ab11-70915a673543.png)

on 절을 활용해 조인 대상을 필터링 할 때, 외부조인이 아니라 내부조인(inner join)을 사용하면, where 절에서 필터링 하는 것과 기능이 동일하다. 따라서 on 절을 활용한 조인 대상 필터링을 사용할 때, 내부조인 이면 익숙한 where 절로 해결하고, 정말 외부조인이 필요한 경우에만 이 기능을 사용하자.


연관관계 없는 엔티티 외부 조인
- 회원의 이름과 팀의 이름이 같은 대상 외부 조인
- JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name

```java
@Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> results = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name)) // member.team이 아닌 team
                .fetch();
        for (Tuple tuple : results) {
            System.out.println(tuple);
        }
    }

```

![image](https://user-images.githubusercontent.com/83503188/185736729-7ea0de19-3acb-4b38-8cb5-1f36f31ed6d0.png)

위의 sql을 보면 1번에서 id에 대한 조건절이 사라진 것을 확인할 수 있다.

주의! 문법을 잘 봐야 한다. leftJoin() 부분에 일반 조인과 다르게 엔티티 하나만 들어간다.
- 일반조인: `.leftJoin(member.team, team).on(member.username.eq(team.name))` 인 경우에는 id조건절 포함 O
- on조인: `.leftJoin(team).on(member.username.eq(team.name))` 인 경우에는 id조건절 포함 X

![image](https://user-images.githubusercontent.com/83503188/185736770-a30beef5-e3e1-48a9-8238-eaa3778f4ca6.png)

위의 `leftjoin()`을 `join()`으로 변경하면 검색조건에 해당하는 대상만 가져온다.

![image](https://user-images.githubusercontent.com/83503188/185736786-c0eadc53-e2bd-47f2-ac7d-ccc588d3d67f.png)

### 조인 - 페치 조인

페치 조인은 SQL에서 제공하는 기능은 아니다. SQL조인을 활용해서 연관된 엔티티를 SQL 한번에 조회하는 기능이다. 주로 성능 최적화에 사용하는 방법이다.

```java
@PersistenceUnit // EntityManagerFactory을 위한 어노테이션
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // team이 이미 로딩된 entity인지 아닌지를 검증하는 Util
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isEqualTo(false);
    }

    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        // team이 이미 로딩된 entity인지 아닌지를 검증하는 Util
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isEqualTo(true);
    }
```

사용방법
- join(), leftJoin() 등 조인 기능 뒤에 fetchJoin() 이라고 추가하면 된다.

## 서브 쿼리

```text
com.querydsl.jpa.JPAExpressions 
```

서브 쿼리의 경우 alias가 겹치면 안되기 때문에 `new Qxxx("")`를 통해 alias를 지정해줘야한다.

### 서브 쿼리 eq 사용

나이가 가장 많은 회원 조회

```java
@Test
    public void subQuery() throws Exception {
        // 서브 쿼리의 경우 alias가 겹치면 안되기 때문에 new Qxxx("")를 통해 alias를 지정해줘야한다.

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub) // -> 40
                ))
                .fetch();

        assertThat(result)
                .extracting("age")
                .containsExactly(40);
    }
```

![image](https://user-images.githubusercontent.com/83503188/185736869-5bdadbd7-8160-4817-92f9-027a4e416ece.png)

### 서브 쿼리 goe 사용

나이가 평균 나이 이상인 회원

```java
 @Test
    public void subQueryGoe() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result)
                .extracting("age")
                .containsExactly(30, 40);
    }
```

### 서브쿼리 여러 건 처리 in 사용

```java
@Test
    public void subQueryIn() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result)
                .extracting("age")
                .containsExactly(20, 30, 40);
    }
```

### select 절에 subquery

```java
@Test
    public void selectSubQuery() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(
                        member.username,
                        select(memberSub.age.avg())
                                .from(memberSub)
                )
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

    }
```

![image](https://user-images.githubusercontent.com/83503188/185736917-183fcfce-bf4c-4adb-967a-c44807ac0709.png)

> from 절의 서브쿼리 한계
>
> JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다. 당연히 Querydsl 도 지원하지 않는다. 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다. Querydsl도 하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다.

from 절의 서브쿼리 해결방안

1. 서브쿼리를 join으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
3. nativeSQL을 사용한다.

## Case 문

select, 조건절(where), order by에서 사용 가능

### 단순한 조건
```java
@Test
    public void basicCase() {
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String st : result) {
            System.out.println("st = " + st);
        }
    }
```

![image](https://user-images.githubusercontent.com/83503188/185737013-62a698d8-8340-457e-9abe-721f274e0240.png)

### 복잡한 조건

```java
 @Test
    public void complexCase1() throws Exception {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String st : result) {
            System.out.println("st = " + st);
        }
    }
```

![image](https://user-images.githubusercontent.com/83503188/185737085-a6893e5f-c519-4940-b32e-d27edaa4ab22.png)


### orderBy에서 Case 문 함께 사용하기 예제

예를 들어서 다음과 같은 임의의 순서로 회원을 출력하고 싶다면?
1. 0 ~ 30살이 아닌 회원을 가장 먼저 출력
2. 0 ~ 20살 회원 출력
3. 21 ~ 30살 회원 출력

```java
@Test
    public void complexCase2() {

        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        List<Tuple> result = queryFactory
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc())
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.println("username = " + username + " age = " + age + " rank = "
                    + rank);
        }
    }
```

![image](https://user-images.githubusercontent.com/83503188/185737091-10afa1c4-4975-4f61-a0bc-b9f2f67e06f4.png)

DB에서는 위와 같은 case는 처리는 하지 않고 row 데이터를 필터링, 그룹핑 정도만 처리하고 데이터를 변환하는 작업은 DB에서 하지 않는 것을 권장한다.

## 상수, 문자 더하기

상수가 필요하면 `Expressions.constant(xxx)` 사용

```java
 @Test
    public void constant() throws Exception {

        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
```

![image](https://user-images.githubusercontent.com/83503188/185737131-c365f8d6-adf3-40a3-bf7b-fb638e2b4128.png)

위와 같이 최적화가 가능하면 SQL에 constant 값을 넘기지 않는다. 상수를 더하는 것 처럼 최적화가 어려우면 SQL에 constant 값을 넘긴다.

### 문자 더하기 concat

{username}_{age}

```java
 @Test
    public void concat() throws Exception {

        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();

        for (String st : result) {
            System.out.println("st = " + st);
        }
    }

```

![image](https://user-images.githubusercontent.com/83503188/185737222-8628911e-fcab-403d-a155-b536fad4b433.png)

member.age.stringValue() 부분이 중요한데, 문자가 아닌 다른 타입들은 stringValue() 로 문자로 변환할 수 있다. 이 방법은 ENUM을 처리할 때도 자주 사용한다.