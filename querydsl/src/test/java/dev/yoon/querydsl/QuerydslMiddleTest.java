package dev.yoon.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.yoon.querydsl.dto.MemberDto;
import dev.yoon.querydsl.dto.QMemberDto;
import dev.yoon.querydsl.dto.UserDto;
import dev.yoon.querydsl.entity.Member;
import dev.yoon.querydsl.entity.QMember;
import dev.yoon.querydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static dev.yoon.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
public class QuerydslMiddleTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;


    @BeforeEach // 각 테스트 전에 실행될 작업
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        Member member5 = new Member("member1", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
    }

    @Test
    public void simpleProjection() throws Exception {

        List<String> result = queryFactory
                .select(member.username) // member.username이 아닌 member인 경우에도 프로젝션 대상이 하나인 것
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }

    }

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

    @Test
    public void findUserDto() throws Exception {
        QMember memberSub = new QMember("memberSub");

        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        QMember.member.username.as("name"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub), "age")

                ))
                .from(QMember.member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

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

    @Test
    public void findUserDtoByConstructor() throws Exception {
        List<UserDto> result = queryFactory
                .select(Projections.constructor(UserDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

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

    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

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
    @Test
    public void bulkAdd() throws Exception {

        long count = queryFactory
                .update(member)
                .set(member.age, member.age.multiply(2))
                .execute();
    }

    @Test
    public void bulkDelete() throws Exception {

        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }

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


}
