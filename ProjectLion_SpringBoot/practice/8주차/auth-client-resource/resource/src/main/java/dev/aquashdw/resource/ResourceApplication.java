package dev.aquashdw.resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * Auth Server에서 발급 받은 토큰을 검증을 함으로써 필요로 하는 데이터를 전송
 * jwt: issuer-uri: jwt를 발급한 곳의 uri 정보
 */
public class ResourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourceApplication.class, args);
	}

}
