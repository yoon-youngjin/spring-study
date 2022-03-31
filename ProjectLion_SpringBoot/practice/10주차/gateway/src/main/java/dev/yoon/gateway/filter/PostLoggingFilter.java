package dev.yoon.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * 요청을 전달하고 돌아온 요청에 header값을 확인하기 위한 필터
 */
//@Component
@Slf4j
public class PostLoggingFilter implements GlobalFilter {

    /**
     * Mono<Void>: 응답이 돌아온 시점에서 값을 가지기 시작하는 객체
     * chain.filter(exchange): 함수를 실행했다고 해서 응답을 가진 객체가 아님을 인지
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /**
         * exchange가 일어나고(=요청이 끝나고)
         */
        return chain.filter(exchange)
                .then(Mono.fromRunnable(()-> {
                    ServerHttpRequest request = exchange.getRequest();
                    String requestId = request.getHeaders()
                            .getFirst("likelion-gateway-request-id");
                    String requestTimeString = request.getHeaders()
                            .getFirst("likelion-gateway-request-time");
                    long timeEnd = Instant.now().toEpochMilli();
                    long timeStart = requestTimeString == null ? timeEnd : Long.parseLong(requestTimeString);

                    log.info("Excution Time id: {}, timediff(ms): {}"
                            ,requestId
                            ,timeEnd - timeStart);
                }));
    }

}
