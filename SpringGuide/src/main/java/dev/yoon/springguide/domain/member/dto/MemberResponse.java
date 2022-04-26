package dev.yoon.springguide.domain.member.dto;

import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.model.Email;
import dev.yoon.springguide.domain.member.model.Name;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private Email email;

    private Name name;

    public MemberResponse(final Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
    }
}
