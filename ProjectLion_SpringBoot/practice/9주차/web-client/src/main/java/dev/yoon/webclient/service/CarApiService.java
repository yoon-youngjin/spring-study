package dev.yoon.webclient.service;

import dev.yoon.webclient.model.CarDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
/**
 * 자동차 정보를 랜덤으로 사용하는 API를 이용하는 Service
 */
public class CarApiService {
    private static final Logger logger = LoggerFactory.getLogger(CarApiService.class);
    private final WebClient randomDataClient;

    public CarApiService(WebClient randomDataClient) {
        this.randomDataClient = randomDataClient;
    }

    public CarDto buyNewCar() {
        ResponseEntity<CarDto> result = this.randomDataClient
                .get()
                .uri("/api/vehicle/random_vehicle")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.empty())
                /**
                 * bodyToMono
                 * reactive programming에서 사용하는 단위의 객체 중 하나
                 * 응답에 대한 body를 어떠한 자바클래스로 해석(매핑)을 할지 전달
                 */
//                .bodyToMono(CarDto.class)
                /**
                 * bodyToMono대신 toEntity를 사용하는 모습
                 */
                .toEntity(CarDto.class)
                .block();

        return result.getBody();
    }
}
