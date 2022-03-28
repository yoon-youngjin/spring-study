package dev.yoon.webclient.service;

import dev.yoon.webclient.model.ActuatorLoggerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
/**
 *  Spring Boot Actuator를 사용하기 위한 Serive
 *  API로 원격에 있는 서버 Actuator에 요청을 보내는 용도
 *  Admin 서버
 *  서버의 상태를 파악하기 위한 admin서비스를 만들기 위해서 actuator를 열어놓은 서버에서 요청을 보내는 client서버(actuatorClient)를 만드는 것이 효과적
 */
public class ActuatorService {
    private static final Logger logger = LoggerFactory.getLogger(ActuatorService.class);
    private final WebClient actuatorClient;

    public ActuatorService(WebClient actuatorClient) {
        this.actuatorClient = actuatorClient;
    }

    /**
     * actuator경로 뒤의 경로중 loggers
     */
    public void setServerLogLevel(String loggerName, String logLevel){
        String uri = String.format("/loggers/%s", loggerName);
        ResponseEntity<?> bodiless = this.actuatorClient
                .post()
                .uri(uri)
                /**
                 * config에서 .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 설정을 통해
                 * ActuatorLoggerDto를 Json으로 해석하여 전송
                 */
                .bodyValue(new ActuatorLoggerDto(logLevel))
                /**
                 * 위의 post, uri, bodyvalue는 요청을 준비하는 과정
                 * retrieve(): 응답을 처리할 방법을 제공
                 */
                .retrieve()
                /**
                 * onStatus(Predicate, )
                 * 400대의 에러가 발생 시 예외 처리
                 */
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    logger.error(clientResponse.statusCode().toString());
                    return Mono.empty();
                })
                /**
                 * onStatus(Predicate, )
                 * 500대의 에러가 발생 시 예외 처리
                 * Mono.error(): 기본적으로 예외가 아닌 자신의 예외를 넣어주는 방법
                 */
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)))
                /**
                 * httpbody가 없는 Entity, body가 있어도 읽지 않음
                 * 응답에서 body를 제외한 나머지 부분(status line, header)들이 정의된 ResponseEntity에 응답을 줌
                 */
                .toBodilessEntity()
                /**
                 * block을 마지막으로 선언해야지 결과가 반환됨
                 * block을 호출함으로써 위의 과정이 완전히 처리가 났을 떄 다음줄로 넘어가라는 뜻
                 */
                .block();
    }

    /**
     * actuator경로 뒤의 경로중 shutdown
     */
    public void shutdownServer() {
        String uri = "/shutdown";
        ResponseEntity<?> bodiless = this.actuatorClient
                .post()
                .uri(uri)
                .retrieve()
                /**
                 * .toEntity()
                 * 실제 Entity를 반환하는 함수,toEntity의 매개변수로 설정한 클래스대로 ResponseEntity가 생성
                 */
//                .toEntity()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new ResponseStatusException(clientResponse.statusCode())))
                .toBodilessEntity()
                .block();
    }
}
