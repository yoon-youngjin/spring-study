package dev.yoon.springguide.domain.member.dto;


import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.domain.ReferralCode;
import dev.yoon.springguide.domain.member.model.Email;
import dev.yoon.springguide.domain.member.model.Name;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public class SignUpRequest {

    @Valid
    private Email email;

    @Valid
    private Name name;

    SignUpRequest(@Valid Email email, @Valid Name name) {
        this.email = email;
        this.name = name;
    }

    public Member toEntity(final ReferralCode referralCode) {
        return Member.builder()
                .name(name)
                .email(email)
                .referralCode(referralCode)
                .build();
    }
}
