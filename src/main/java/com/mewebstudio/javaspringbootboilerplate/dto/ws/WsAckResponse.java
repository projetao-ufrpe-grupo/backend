package com.mewebstudio.javaspringbootboilerplate.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WsAckResponse {
    private String status;
    private String messageId;
    private String from;
    private String to;
    private String type;
    private long ackAt;
}