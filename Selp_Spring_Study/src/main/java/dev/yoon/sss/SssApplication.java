package dev.yoon.sss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SssApplication {

    public static void main(String[] args) {
        SpringApplication.run(SssApplication.class, args);
    }

}
