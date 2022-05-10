package dev.yoon.springguide;

import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
public class SampleApi {

  private final RestTemplate localTestTemplate;

  @PostMapping("/local-sign-up")
  public Member test(@RequestBody @Valid final SignUpRequest dto) {

    final ResponseEntity<Member> responseEntity = localTestTemplate
        .postForEntity("/members", dto, Member.class);


    final Member member = responseEntity.getBody();
    return member;
  }
}
