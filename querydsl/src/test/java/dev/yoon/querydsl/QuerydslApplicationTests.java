package dev.yoon.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.yoon.querydsl.entity.Hello;
import dev.yoon.querydsl.entity.QHello;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// Test에 Transactional 있는 경우 기본적으로 모두 Rollback처리
@SpringBootTest
@Transactional
@Commit
class QuerydslApplicationTests {

    @PersistenceContext
    EntityManager em;


    @Test
    void contextLoads() {

        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QHello qHello = QHello.hello;
        Hello result = queryFactory
                .selectFrom(qHello)
                .fetchOne();

        Assertions.assertThat(result).isEqualTo(hello);
        Assertions.assertThat(result.getId()).isEqualTo(hello.getId());


    }

}
