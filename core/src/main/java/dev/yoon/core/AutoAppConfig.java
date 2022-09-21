package dev.yoon.core;

import dev.yoon.core.member.MemberRepository;
import dev.yoon.core.member.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
// @Component 가 붙은 클래스를 찾아서 모두 자동으로 스프링 빈으로 등록해준다.
// excludeFilters -> 제외할 Component 지정
@ComponentScan(
        basePackages = "dev.yoon.core",
//        basePackageClasses = AutoAppConfig.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

    @Bean(name = "memoryMemberRepository")
    MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }


}
