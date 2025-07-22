package com.mewebstudio.javaspringbootboilerplate.dto.ws;

import com.mewebstudio.javaspringbootboilerplate.dto.response.ChatMessageDTO;

import java.util.List;

public record ChatConversationDTO(
        List<ParticipanteDTO> participantes,
        List<ChatMessageDTO> ultimasMensagens
) {}