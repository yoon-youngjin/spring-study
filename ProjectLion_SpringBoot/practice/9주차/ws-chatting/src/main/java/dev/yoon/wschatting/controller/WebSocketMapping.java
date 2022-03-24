package dev.yoon.wschatting.controller;

import dev.yoon.wschatting.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@Slf4j
@RequiredArgsConstructor
/**
 * 실제로 동작하는 부분
 */
public class WebSocketMapping {

    /**
     * SimpMessagingTemplate
     *
     */
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * @MessageMapping
     * WebSocket에서 사용하는 Mapping
     */
    @MessageMapping("/ws/chat")
    public void sendChat(ChatMessage chatMessage){
        log.info(chatMessage.toString());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatMessage.setTime(time);
        simpMessagingTemplate.convertAndSend(
                String.format("/receive-endpoint/%s", chatMessage.getRoomId()),
                chatMessage
        );
    }

    /**
     * 전체 채팅방
     * destination을 상황에 따라 변경할 필요가 없을 경우
     */
//    @MessageMapping("/ws/chat")
//    @SendTo("/receive-endpoint/all")
//    private ChatMessage sendChatAll(){
//
//    }
}
