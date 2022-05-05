package dev.yoon.springguide.domain.member.api;

import dev.yoon.springguide.domain.dao.MemberFindDao;
import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.dto.MemberExistenceType;
import dev.yoon.springguide.domain.member.dto.MemberProfileUpdate;
import dev.yoon.springguide.domain.member.dto.MemberResponse;
import dev.yoon.springguide.domain.member.dto.SignUpRequest;
import dev.yoon.springguide.domain.member.service.MemberProfileService;
import dev.yoon.springguide.domain.member.service.MemberSearchService;
import dev.yoon.springguide.domain.member.service.MemberSignUpService;
import dev.yoon.springguide.global.common.response.Existence;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberApi {

    private final MemberSignUpService memberSignUpService;
    private final MemberFindDao memberFindDao;
    private final MemberProfileService memberProfileService;
    private final MemberSearchService memberSearchService;

    @PostMapping
    public MemberResponse create(@RequestBody @Valid final SignUpRequest dto) {
        final Member member = memberSignUpService.doSignUp(dto);
        return new MemberResponse(member);
    }

    @GetMapping("/{id}")
    public MemberResponse getMember(@PathVariable long id) {
        return new MemberResponse(memberFindDao.findById(id));
    }

    @PutMapping("/{id}/profile")
    public void updateProfile(@PathVariable long id, @RequestBody @Valid final MemberProfileUpdate dto) {
        memberProfileService.update(id, dto);
    }

    @GetMapping("/existence")
    public Existence isExistTarget(
            @RequestParam("type") final MemberExistenceType type,
            @RequestParam(value = "value", required = false) final String value
    ) {
        return new Existence(memberSearchService.isExistTarget(type, value));
    }

}
