package dev.yoon.springguide.domain.dao;

import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberFindDao {

    private final MemberRepository memberRepository;

    public Member findById(final Long id) {
        final Optional<Member> member = memberRepository.findById(id);
        member.orElseThrow(() -> new MemberNotFoundException(id));
        return member.get();
    }
}
