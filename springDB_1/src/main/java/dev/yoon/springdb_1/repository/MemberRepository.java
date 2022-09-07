package dev.yoon.springdb_1.repository;

import dev.yoon.springdb_1.domain.Member;

public interface MemberRepository {
    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, int money);

    void delete(String memberId);
}