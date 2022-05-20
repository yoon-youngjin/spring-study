package dev.yoon.springguide.domain.member.dto;


import dev.yoon.springguide.domain.member.model.Email;
import dev.yoon.springguide.domain.member.model.Name;

public class SignUpRequestBuilder {

  public static SignUpRequest build(Email email, Name name) {
    return new SignUpRequest(email, name);
  }


}