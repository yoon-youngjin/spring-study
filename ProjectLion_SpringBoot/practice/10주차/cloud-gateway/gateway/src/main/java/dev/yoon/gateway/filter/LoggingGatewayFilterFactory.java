package dev.yoon.gateway.filter;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * 커스텀 필터 작성
 * yml파일에 적용해야함
 *
 * {이름}GatewayFilterFactory
 */
@Component
@Slf4j
public class LoggingGatewayFilterFactory
        extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    /**
     * 이름 등록 (Logging)
     */
    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }
    /**
     * name: RewritePath
     *               args:
     *                 regexp: /api/(?<path>.*)
     *                 replacement: /$\{path}
     * args가 커스텀 필터에서 정의해야하는 클래스
     * Config의 필드가 해당 필터에 필요한 인자
     */
    @Override
    public GatewayFilter apply(LoggingGatewayFilterFactory.Config config) {
        /**
         * GlobalFilter에서 filter 함수와 같은 구조
         * 앞의 PreLoggingFilter와 PostLoggingFilter를 합쳐봄
         *
         * pre:
         */
        return (((exchange, chain) -> {
            log.trace(config.toString());
            /**
             * 앞부분만 따서 uid로 설정
              */
            String uid = config.simpleUid?
                    UUID.randomUUID().toString().split("-")[0] :
                    UUID.randomUUID().toString();
            final long timeStart = Instant.now().toEpochMilli();

            return chain.filter(exchange)
                    /**
                     * post: 처음 요청 다시 필터로 돌아온 상태
                     */
                    .then(Mono.fromRunnable(()->{
                        long timediff = Instant.now().toEpochMilli() - timeStart;
                        /**
                         * inSeconds: 초로 설정해달라는 설정
                         */
                        if (config.inSeconds)
                            timediff/=1000;
                        log.info("Excution Time id:{}, timediff({}): {}",
                                uid,
                                config.inSeconds ? "s" :"ms",
                                timediff);
                    }));
        }));
    }

    /**
     * 이름 재정의하는 함수
     * 이름을 재정의하지 않으면 {이름}GatewayFilterFactory에서 {이름}으로 적용
     */
    @Override
    public String name() {
        return "LogExecution";
    }

    @Data
    public static class Config {
        private boolean simpleUid;
        private boolean inSeconds;
    }
}
