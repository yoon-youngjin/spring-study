package dev.yoon.core.autowired;

import dev.yoon.core.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class AutowiredTest {

    @Test
    public void AutoWiredOption() throws Exception {

        ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);

    }

    static class TestBean {

        // 현재 Member 객체는 스프링에 의해 관리되는 객체가 아니다.
        @Autowired(required = false)
        public void setNoBean1(Member member) {
            System.out.println("member1 = " + member);
        }

        @Autowired
        public void setNoBean2(@Nullable Member member) {
            System.out.println("member2 = " + member);
        }

        @Autowired
        public void setNoBean3(Optional<Member> optionalMember) {
            System.out.println("member3 = " + optionalMember);
        }



    }
}
