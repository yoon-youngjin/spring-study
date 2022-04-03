package dev.yoon.gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 라우팅 설정 파일
 * 자바소스코드가 아닌 yml파일로 설정 가능하다.
 */
//@Configuration
public class RoutingConfig {

    /**
     * RouteLocator를 반환하는 스프링 관리 하의 객체
     * RouteLocatorBuilder ?
     * 스프링 클라우드 게이트웨이에서 사용하는 LocatorBuilder로써 builder 매개변수에 설정을 주입
     */
//    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                /**
                 * roote(id,predicate())
                 * id: 어떤 용도로 사용하는 route인지 구분하는 구분자
                 * predicate(): 요청 받은 주소가 routing을 진행해야하는 경로인지 참, 거짓 파악
                 */
                .route("community-shop", predicate -> {
                    return predicate
                            /**
                             * shop으로 시작하는 경로인지 파악
                             */
                            .path("/api/shop/**")
                            /**
                             * 들어오는 경로를 변경하고 싶은 경우? /api/shop -> /shop/
                             * rewritePath
                             * 정규 표현식을 통해 경로를 변경해주는 함수
                             */
                            .filters(filter -> filter
                                    .rewritePath(
                                            "/api/(?<path>.*)",
                                            "/${path}"
                                    ))
                            /**
                             * 목적지
                             */
                            .uri("http://localhost:8081");
                })
                .build();
    }
}
