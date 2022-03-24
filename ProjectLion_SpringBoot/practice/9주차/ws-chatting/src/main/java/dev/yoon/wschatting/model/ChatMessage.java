package dev.yoon.wschatting.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class ChatMessage {

    private String roomId;
    private String sender;
    private String message;
    private String time;


}
