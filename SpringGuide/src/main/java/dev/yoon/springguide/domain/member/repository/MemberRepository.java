package dev.yoon.springguide.domain.member.repository;

import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.domain.ReferralCode;
import dev.yoon.springguide.domain.member.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberSupportRepository,
        MemberPredicateExecutor<Member> {

    Optional<Member> findByEmail(Email email);

    boolean existsByEmail(Email email);

    boolean existsByReferralCode(ReferralCode referralCode);

}
