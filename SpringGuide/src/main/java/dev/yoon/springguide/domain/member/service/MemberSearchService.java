package dev.yoon.springguide.domain.member.service;

import com.querydsl.jpa.JPQLQuery;
import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.domain.QMember;
import dev.yoon.springguide.domain.member.domain.ReferralCode;
import dev.yoon.springguide.domain.member.dto.MemberExistenceType;
import dev.yoon.springguide.domain.member.model.Email;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberSearchService extends QuerydslRepositorySupport {

    public MemberSearchService() {
        super(Member.class);
    }

    public boolean isExistTarget(final MemberExistenceType type, final String value) {

        QMember qMember = QMember.member;
        JPQLQuery<Member> query;

        switch (type) {
            case EMAIL:
                query = from(qMember)
                        .where(qMember.email.eq(Email.of(value)));
                break;
            case REFERRAL_CODE:
                query = from(qMember)
                        .where(qMember.referralCode.eq(ReferralCode.of(value)));

                break;

            default:
                throw new IllegalArgumentException(String.format("%s is not valid", type.name()));
        }

        final Member member = query.fetchFirst();
        return member != null;

    }

}
