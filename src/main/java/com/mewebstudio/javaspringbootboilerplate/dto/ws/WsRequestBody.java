package com.mewebstudio.javaspringbootboilerplate.dto.ws;

import com.mewebstudio.javaspringbootboilerplate.entity.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WsRequestBody {
    String from;

    String to;

    String content;

    String type;

    long date;

    public ChatMessage transformToChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFromUser(UUID.fromString(from));
        chatMessage.setToUser(UUID.fromString(to));
        chatMessage.setContent(content);
        chatMessage.setType(type);
        chatMessage.setSentAt(java.time.OffsetDateTime.now());
        chatMessage.setStatus("SENT"); // Default status
        return chatMessage;
    }
}