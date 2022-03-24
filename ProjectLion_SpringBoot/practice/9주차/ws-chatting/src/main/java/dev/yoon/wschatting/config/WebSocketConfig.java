package dev.yoon.wschatting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
/**
 * EnableWebSocketMessageBroker
 * 스프링 어플리케이션에서 STOMP 기반의 웹 소켓 메세징이 지원
 */
@EnableWebSocketMessageBroker
/**
 * WebSocket 설정
 */
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 웹 소켓 기준에서 END POINT를 지정
     * 다수의 클라이언트들이 최초로 접속하는 웹 소켓 END POINT 지정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        /**
         * 최초로 /ws/chat 경로에 요청을 보냄
         */
        registry.addEndpoint("/ws/chat");
        /**
         * withSockJS
         * SockJS
         * 웹 소켓이 가능한 브라우저에서는 웹 소켓을 반환하는데
         * 모든 브라우저가 가능한 것이 아니므로
         * 웹 소켓처럼 작동하도록 하는 객체
         */
        registry.addEndpoint("/ws/chat").withSockJS();
    }

    /**
     *
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /**
         * STOMP는 destination이라는 헤더를 통해
         * 어떤 클라이언트에 요청이 가게될 것인지를 정의
         * enableSimpleBroker
         * 메세지를 클라이언트에게 보내기 위한 브로커 생성, 클라이언트는 해당 destination(/receive-endpoint)을 구독함으로서 전달하는 메세지의 일부를 들을 수 있음
         * setApplicationDestinationPrefixes
         * application에서 응답을 받기 위해서 destination 적용
         * 클라이언트쪽에서 서버에 메세지를 보낼 경우 destination(/send-endpoint)으로 시작할 것 이다.
         */
        registry.enableSimpleBroker("/receive-endpoint");
        registry.setApplicationDestinationPrefixes("/send-endpoint");
    }
}
