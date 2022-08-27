package dev.yoon.springdb_1.repository;

import dev.yoon.springdb_1.domain.Member;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class MemberRepositoryV0Test {
    Logger log = LoggerFactory.getLogger(MemberRepositoryV0Test.class);

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV1", 10000);
        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);

        // why ? 성공 -> isEqualTo()는 `==`가 아닌 .eqauls() 메서드를 통해 비교하는데
        // 현재 Lombok의 @Data 어노테이션을 사용하여 eqauls가 재정의된 상태이다.
        // 따라서 현재는 값만 같으면 같은 객체로 인식하는 것
        log.info("member != findMember ", member == findMember); // false
        log.info("member.equals(findMember) ", member.equals(findMember)); // true
        assertThat(findMember).isEqualTo(member);

        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

    }
}