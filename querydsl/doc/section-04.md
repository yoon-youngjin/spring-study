# 중급 문법

## 프로젝션과 결과 반환 - 기본

> 프로젝션?
>
> select 대상을 지정하는 것 

- 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
- 프로젝션 대상이 둘 이상이면 튜플이나 DTO로 조회

### 튜플 조회

프로젝션 대상이 둘 이상일 때 사용

```java
@Test
    public void tupleProjection() throws Exception {

        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);

            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }
```

### 프로젝션과 결과 반환 - DTO 조회

**MemberDto**

```java
@Data
@NoArgsConstructor
public class MemberDto {

    private String username;
    private int age;

}
```

순수 JPA에서 DTO 조회 코드

```java
@Test
    public void findDtoByJPQL() throws Exception {
        List<MemberDto> result = em.createQuery(
                        "select new dev.yoon.querydsl.dto.MemberDto(m.username, m.age) " +
                                "from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
```

- 순수 JPA에서 DTO를 조회할 때는 new 명령어를 사용해야함
- DTO의 package이름을 다 적어줘야해서 지저분함
- 생성자 방식만 지원함

### Querydsl 빈 생성(Bean population)

결과를 DTO 반환할 때 사용, 다음 3가지 방법 지원
#### 1. 프로퍼티 접근
````java
@Test
    public void findDtoBySetter() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
````

#### 2. 필드 직접 접근
```java
@Test
    public void findDtoByField() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
```
#### 3. 생성자 사용
```java
@Test
    public void findDtoByConstructor() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
```

#### 별칭이 다른 경우?

**UserDto**

```java
@Data
@NoArgsConstructor
public class UserDto {

    private String name;
    private int age;

    public UserDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

```java
@Test
    public void findUserDtoBySetter() throws Exception {
        List<UserDto> result = queryFactory
                .select(Projections.bean(UserDto.class,
                        member.username.as("name"),
                        member.age))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }
```

- 프로퍼티나, 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
- ExpressionUtils.as(source,alias) : 필드나, 서브 쿼리에 별칭 적용
- username.as("memberName") : 필드에 별칭 적용

## 프로젝션과 결과 반환 - @QueryProjection

**생성자 + @QueryProjection**

```java
@Data
@NoArgsConstructor
public class MemberDto {

    private String username;
    private int age;

    @QueryProjection
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

- ./gradlew compileQuerydsl
- QMemberDto 생성 확인

@QueryProjection 활용

```java
@Test
public void findDtoByQueryProjection() throws Exception {
        List<MemberDto> result = queryFactory
        .select(new QMemberDto(member.username, member.age))
        .from(member)
        .fetch();

        for (MemberDto memberDto : result) {
        System.out.println("memberDto = " + memberDto);
        }
}
```

이 방법은 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법이다. 다만 DTO에 QueryDSL 어노테이션을 유지해야 하는 점과 DTO까지 Q 파일을 생성해야 하는 단점이 있다.

## distinct

```java
    @Test
    public void distinct() throws Exception {

        List<String> result = queryFactory
                .select(member.username).distinct()
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
```

## 동적 쿼리 - BooleanBuilder 사용

동적 쿼리를 해결하는 두가지 방식
### 1. BooleanBuilder
```java
@Test
    public void dynamicQuery_BooleanBuilder() throws Exception {

        // username == `member1`, age == 10인 Member를 찾고 싶은 경우
        // username이 null인 경우 age에 대한 조건만 age가 null인 경우 username 조건만
        String username = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(username, ageParam);
        assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {

        BooleanBuilder builder = new BooleanBuilder(member.username.eq(
                usernameCond
        ));
        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();

    }
```

###2. **Where 다중 파라미터 사용**

```java
@Test
    public void dynamicQuery_whereParam() throws Exception {
        String username = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(username, ageParam);
        assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {

        return queryFactory
                .selectFrom(member)
                .where(
                        usernameEq(usernameCond), ageEq(ageCond)
//                        allEq(usernameCond, ageCond)
                )
                .fetch();

    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

```

- where 조건에 null 값은 무시된다.
- 메서드를 다른 쿼리에서도 재활용 할 수 있다.
- 쿼리 자체의 가독성이 높아진다.

#### 조합 가능
```java
private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

```

## 수정, 삭제 벌크 연산

쿼리 한번으로 대량 데이터 수정

```java
 @Test
    public void bulkUpdate() throws Exception {

        // member1 = 10 -> 비회원
        // member2 = 20 -> 비회원

        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute(); // 영향을 받은 row 수를 반환

        em.flush();
        em.clear();

        assertThat(count).isEqualTo(2);

        // 영속성 컨텍스트 | DB
        // member1 = 10 -> "member1" | member1 = 10 -> "비회원"
        // member2 = 20 -> "member2" | member2 = 20 -> "비회원"
        // member3 = 30 -> "member3" | member3 = 30 -> "member3"
        // member4 = 40 -> "member4" | member4 = 40 -> "member4"

        // Bulk 연산의 경우 영속성 컨텍스트를 거치지 않고 DB에 바로 쿼리를 날린다.
        // 따라서 DB의 상태와 영속성 컨텍스트의 상태가 달라진다.

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        // 위의 코드는 DB에 쿼리를 날려 가져오는데 (비회원, 비회원, member3, member4로 가져옴)
        // JPA는 DB에 가져온 결과를 영속성 컨텍스트에 다시 넣어주는 과정을 거친다.
        // 하지만 DB에서 조회한 결과가 영속성 컨텍스트에 존재하기 때문에 DB에서 Select 해왔어도 DB에서 가져온 결과를 버리고 영속성 컨텍스트의 결과로 대체한다.
        // 영속성 컨텍스트가 항상 우선권을 가진다.

       for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }

    }
```

- Bulk 연산의 경우 영속성 컨텍스트를 거치지 않고 DB에 바로 쿼리를 날린다.
- 따라서 DB의 상태와 영속성 컨텍스트의 상태가 달라진다.
- Bulk 연산 이후에는 반드시 `em.flush()`, `em.clear()` 를 통해 DB의 상태와 영속성 컨텍스트의 상태를 맞추자.

기존 숫자에 1 더하기

```java
@Test
    public void bulkAdd() throws Exception {

        long count = queryFactory
                .update(member)
                .set(member.age, member.age.multiply(2))
                .execute();
    }
```

쿼리 한번으로 대량 데이터 삭제

```java
@Test
    public void bulkDelete() throws Exception {

        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }
```

## SQL function 호출하기

SQL function은 JPA와 같이 Dialect에 등록된 내용만 호출할 수 있다.

member M으로 변경하는 replace 함수 사용

```java
@Test
    public void sqlFunction() throws Exception {

        // 멤버 이름에서 member를 M으로 변경하여 조회

       List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                        "function('replace', {0}, {1}, {2})",
                        member.username, "member", "M"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
```

소문자로 변경해서 비교해라.

```java
@Test
    public void sqlFunction2() throws Exception {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
//                .where(member.username.eq(
//                        Expressions.stringTemplate("function('lower', {0})", member.username)))
                .where(member.username.eq(member.username.lower()))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
```

