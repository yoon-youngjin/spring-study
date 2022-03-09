package dev.yoon.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
// profile에 따라서 생성되는 bean이 다르다.
// component어노테이션이 된 클래스는 스프링 부트 앱이 실행될 때 컨스트럭트되어서 ioc컨테이너로 넘어간다.
@Profile("test")
@Slf4j
public class ProfileComponent {

    public ProfileComponent() {
        log.info("profile component profile:local");

    }

}
