package dev.yoon.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    // API 호출 시 헤더에 로그인 시 받은 토큰을 전달해주는 작업 진행
    // 토큰 존재 ? 적절한 인증 ? 토큰 제대로 발급 ?, ...
    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 헤더에 존재하는 검증
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            // jwt 검증
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);

        }));
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        // JWT subject 를 추출하여 검증
        String subject = null;

        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        } catch (Exception e) { // 파싱 중 오류 처리
            System.out.println(e);
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

//        if (!subject.equals()) {
//            returnValue = false;
//        }


        return returnValue;

    }

    // Spring Cloud Gateway Service 는 기존의 Spring MVC로 구성하지 않는다.
    // HttpServletRequest, HttpServletResponse 를 사용할 수 있는 Spring MVC이 아닌 Spring Web Flux 를 사용함으로써 비동기 방식으로 데이터를 처리하게된다.
    // 비동기 방식에서 데이터를 처리하는 2가지 방법 중 하나인 Mono(단일값) -> Mono라는 단일값에 데이터를 넣어서 반환할 수 있다.
    // 단일값이 아닌 다중값 데이터에 대해서는 Flux 라는 형태로 반환

    // 에러 메시지 반환
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }

    public static class Config {

    }
}
