package dev.aquashdw.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 시간 상 편법
 * 프로젝트를 여러 개 나눠서 MSA를 구현하는게 아닌
 * 다양한 설정파일과 @Profile을 통해 MSA처럼 작동하도록 함
 */
@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
