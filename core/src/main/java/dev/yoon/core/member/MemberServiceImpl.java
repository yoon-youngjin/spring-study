package dev.yoon.core.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberServiceImpl implements MemberService {

    private MemberRepository memberRepository;

    @Autowired // 자동 의존관계 주입 -> MemberRepository 타입에 맞는 빈을 찾아서 자동으로 주입
    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }


    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
