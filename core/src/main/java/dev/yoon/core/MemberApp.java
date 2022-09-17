package dev.yoon.core;

import dev.yoon.core.member.Grade;
import dev.yoon.core.member.Member;
import dev.yoon.core.member.MemberService;
import dev.yoon.core.member.MemberServiceImpl;

public class MemberApp {

    public static void main(String[] args) {

        MemberService memberService = new MemberServiceImpl();
        Member member = new Member(1L, "yoon", Grade.VIP);
        memberService.join(member);

        Member findMember = memberService.findMember(1L);
        System.out.println("new Member = " + member.getName());
        System.out.println("find Member = " + findMember.getName());

    }
}
