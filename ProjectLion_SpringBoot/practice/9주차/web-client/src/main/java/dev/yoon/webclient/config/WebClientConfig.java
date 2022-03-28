package dev.yoon.webclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * yml파일에 정의된 access key값
     * 웹 클라이언트의 구현체 생성
     */
    @Value("${ncp.api.access-key:stub-api-key}")
    private String accessKey;

    @Bean
    public WebClient defaultWebClient() {
        /**
         * create(): 가장 기본값의 WebClient
         */
        return WebClient.create();
    }

    /**
     * actuator 서비스에서 사용하기 위한 web Client
     */
    @Bean
    public WebClient actuatorClient() {

        /**
         * 해당 Bean에서 반환되는 모든 요청이 http://localhost:8081/actuator해당 경로에서 시작
         */
        return WebClient.builder()
                .baseUrl("http://localhost:8081/actuator")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    /**
     * ncp 서비스에서 사용하기 위한 WebClient
     */
    @Bean
    public WebClient ncpWebClient() {
        return WebClient.builder()
                .defaultHeader("x-ncp-iam-access-key", accessKey)
                .build();
    }

    /**
     * Json의 키값이 snake_case을 미리 전환하여 자바 객체로 넘겨주는 부분
     * Jackson 사용
     */
    @Bean
    public WebClient randomDataClient(ObjectMapper baseConfig) {

        /**
         * 기본 설정 복사
         */
        ObjectMapper newMapper = baseConfig.copy();
        /**
         * Json의 키들을 어떻게 이름을 바꿀지에 대한 전략을 지정
         */
        newMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        /**
         * 해당 전략을 WebClient에 전달해주기 위한 객체
         * WebClient가 받아들이는 전략
         */
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(newMapper));
                })
                .build();

        return WebClient.builder()
                .baseUrl("https://random-data-api.com")
                .exchangeStrategies(exchangeStrategies)
                .build();
    }



}
