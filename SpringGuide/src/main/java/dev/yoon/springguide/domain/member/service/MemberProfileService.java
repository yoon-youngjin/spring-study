package dev.yoon.springguide.domain.member.service;

import dev.yoon.springguide.domain.member.dao.MemberFindDao;
import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.dto.MemberProfileUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberFindDao memberFindDao;

    public Member update(final long memberId, final MemberProfileUpdate dto) {
        final Member member = memberFindDao.findById(memberId);
        member.updateProfile(dto.getName());
        return member;
    }

}
