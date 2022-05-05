package dev.yoon.springguide.domain.member.dto;

import dev.yoon.springguide.domain.member.model.Name;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class MemberProfileUpdate {

    @Valid
    private Name name;

    public MemberProfileUpdate(@Valid Name name) {
        this.name = name;
    }
}
