package dev.yoon.springguide.domain.member.service;

import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.domain.ReferralCode;
import dev.yoon.springguide.domain.member.dto.SignUpRequest;
import dev.yoon.springguide.domain.member.exception.EmailDuplicateException;
import dev.yoon.springguide.domain.member.dao.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberSignUpService {

    private final MemberRepository memberRepository;

    public Member doSignUp(final SignUpRequest dto) {

        System.out.println("Check123");

        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDuplicateException(dto.getEmail());
        }

        final ReferralCode referralCode = generateUniqueReferralCode();
        return memberRepository.save(dto.toEntity(referralCode));
    }

    private ReferralCode generateUniqueReferralCode() {
        ReferralCode referralCode;
        do {
            referralCode = ReferralCode.generateCode();
        } while (memberRepository.existsByReferralCode(referralCode));

        return referralCode;
    }



}
