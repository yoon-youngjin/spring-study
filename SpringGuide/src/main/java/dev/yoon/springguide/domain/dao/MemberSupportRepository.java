package dev.yoon.springguide.domain.dao;


import dev.yoon.springguide.domain.member.domain.Member;
import dev.yoon.springguide.domain.member.model.Email;

import java.util.List;

public interface MemberSupportRepository {

  List<Member> searchByEmail(Email email);
}
