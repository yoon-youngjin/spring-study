package dev.yoon.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * filter를 만드는 2가지 방법
 * 1. 글로벌 필터: 모든 요청에 대해 필터링
 * 2. 설정용 필터(ex) rewritepath)
 *
 * 글로벌 필터를 구현
 */
//@Component
@Slf4j
public class PreLoggingFilter implements GlobalFilter {


    /**
     * exchange: 필터를 실행할 떄 주고 받게 될 요청과 응답이 담긴 변수
     * chain: 모든 chain들을 관리하는 변수
     *
     * 하나의 요청은 수 많은 MSA를 거쳐서 요청이 전달될 것
     * 돌아왔을 때 요청이 처음 요청이 맞는지를 확인해야함 -> header에서 관리
     */
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {
        log.trace("Executed filter in PreLoggingFilter");
        ServerHttpRequest httpRequest = exchange.getRequest();
        /**
         * mutate(): 변경가능한 함수
         * http요청이 전달되기전에 조작 가능 -> 요청 들어가기전에 header 세팅
         */
        httpRequest.mutate()
                .headers(httpHeaders -> {
                    httpHeaders.add(
                            "likelion-gateway-request-id",
                            UUID.randomUUID().toString());
                    httpHeaders.add(
                            "likelion-gateway-request-time",
                            String.valueOf(Instant.now().toEpochMilli()));

                })
                .build();
//        ServerHttpResponse httpResponse = exchange.getResponse();

        return chain.filter(exchange);
    }
}
