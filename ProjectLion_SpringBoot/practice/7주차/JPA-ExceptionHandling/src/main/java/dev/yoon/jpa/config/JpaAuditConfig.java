package dev.yoon.jpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
/**
 * test를 진행하며 jpa기능을 사용하게됨 test를 할 때 springboot app전체를 구성하기에는 너무 heavy
 * 해당 어노테이션으로 몇개의 class만 선택적으로 실행 가능
  */
@EnableJpaAuditing
public class JpaAuditConfig {
}
