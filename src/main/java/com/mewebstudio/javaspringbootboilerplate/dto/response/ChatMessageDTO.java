package com.mewebstudio.javaspringbootboilerplate.dto.response;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {

    private Long id;

    private UUID fromUserId;
    private String fromUserName;

    private UUID toUserId;
    private String toUserName;

    private String content;
    private String type;

    private OffsetDateTime date;
}