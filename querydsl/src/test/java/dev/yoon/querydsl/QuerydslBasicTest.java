package dev.yoon.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.yoon.querydsl.entity.Member;
import dev.yoon.querydsl.entity.QMember;
import dev.yoon.querydsl.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static dev.yoon.querydsl.entity.QMember.*;
import static dev.yoon.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    // JPAQueryFactory 경우 필드 레벨로 가져가도 괜찮다.
    // 동시성 문제(= 여러 쓰레드에서 동시 접속)?
    // 동시성 문제가 일어나지 않도록 설계되있으므로 안심하고 사용해도 된다.
    // 그리고 Spring Framework가 주입해주는 EntityManager 자체가 Multi-Thread에 아무 문제없도록 설계되어있다고 한다.
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
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        // member1을 찾아라.
        String qlString = "select m from Member m where m.username =:username";
        Member findByJPQL = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findByJPQL.getUsername()).isEqualTo("member1");
    }

    // JPQL에서 Query String 은 문자열로 작성되어 실행시점에 오류를 잡을 수 있다.
    // 따라서 사용자가 해당 쿼리를 실행하는 런타임에 오류가 나게되는 최악의 경우가 발생한다.
    // 단, 인텔리제이 엔터프라이즈 버전을 사용 중인 경우에는 어느정도의 오류를 알려준다.
    @Test
    public void startQuerydsl() {

        Member findByQuerydsl = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) // 위의 setParameter 와 같은 파라미터 바인딩 처리
                .fetchOne();

        assertThat(findByQuerydsl.getUsername()).isEqualTo("member1");

    }

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

        // fetchResult는 getTotal, getLimit, getOffset을 제공 -> count 쿼리를 가져오기 위해 2개의 쿼리 발생
        // fetchResult는 paging Query가 복잡해지면 contents를 가져오는 쿼리와 count 쿼리가 달라지는 문제가 발생할 수 있다.(성능 이슈) -> 복잡하고 성능이 중요한 페이징 처리의 경우 따로 쿼리를 날려줘야한다.
        // fetchResult는 Deprecated 되었으므로 fetch를 통해 content를 조회하고
        // count Query를 통해 total count를 다시 조회해야 한다.
        results.getTotal();
        results.getLimit();
        results.getOffset();
        List<Member> content = results.getResults();

        // count 쿼리
        // fetchCount()는 개발자가 작성한 select 쿼리를 기반으로 count용 쿼리를 내부에서 만들어서 실행한다.
        // 따라서 단순한 쿼리에서는 잘 동작하지만, 복잡한 쿼리에서는 제대로 동작하지 않는다.
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();

        long totalCount = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        System.out.println("totalCount = " + totalCount);
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단, 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
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

    // 전체 조회수가 필요한 경우
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

    // 데이터 타입이 여러개인 경우 Querydsl은 Tuple 타입으로 제공한다.
    // 실무에서는 Tuple을 잘 사용하지 않고 Dto를 통해 직접 조회하는 방법을 사용한다.
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

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     */
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

    /**
     * 팀 A에 소속된 모든 회원
     */
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

    /**
     * theta-join: 연관관계가 없어도 join
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
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

    /**
     * 예) 회원과 팀을 Join 하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: selet m, t from Member m left join m.team t on t.name =: 'teamA'
     */
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

    /**
     * 연관관계가 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 회원 외부 조인
     */
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


    /**
     * 나이가 가장 많은 회원 조회
     */
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

    /**
     * 나이가 평균 이상인 회원 조회
     */
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

    /**
     * 나이가 10살 초과인 회원 조회
     */
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

    @Test
    public void concat() throws Exception {

        //{username}_{age}
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();

        for (String st : result) {
            System.out.println("st = " + st);
        }
    }

}
