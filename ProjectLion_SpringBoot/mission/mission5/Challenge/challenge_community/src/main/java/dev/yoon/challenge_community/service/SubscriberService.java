package dev.yoon.challenge_community.service;

import dev.yoon.challenge_community.model.CookieProcess;
import dev.yoon.challenge_community.repository.LogoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/**
 * 이미 있는 Bean객체에서 받아와서 name을 가져옴
 * Config에서 정의한 큐를 받아옴
 */
@RabbitListener(queues = "#{autoGenQueue.name}")
@RequiredArgsConstructor
public class SubscriberService {

    private final LogoutRepository logoutRepository;

    @RabbitHandler
    public void receiveMessage(String cookie_value) {

        CookieProcess cookieProcess = new CookieProcess(cookie_value, "");
        logoutRepository.save(cookieProcess);

        log.info("Received: {}", cookie_value);

    }


}
