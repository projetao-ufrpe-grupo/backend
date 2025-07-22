package com.mewebstudio.javaspringbootboilerplate.controller;

import com.mewebstudio.javaspringbootboilerplate.dto.response.ChatMessageDTO;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.ChatConversationDTO;
import com.mewebstudio.javaspringbootboilerplate.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "007. Chat", description = "Chat API")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/conversation")
    @Operation(
            summary = "Buscar conversa entre dois usuários",
            description = "Retorna as mensagens trocadas entre dois usuários, com suporte a paginação. Requer autenticação via token JWT.",
            security = @SecurityRequirement(name = "bearerAuth") // ou o nome que você configurou
    )
    public ResponseEntity<Page<ChatMessageDTO>> getMessages(@RequestParam String fromUserId,
                                                            @RequestParam String toUserId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessageDTO> messages = chatService.getConversation(fromUserId, toUserId, page, size);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversations")
    @Operation(
            summary = "Buscar todas as conversas do usuário",
            description = """
                        Retorna as últimas mensagens de todas as conversas em que o usuário autenticado participou, agrupadas por parceiro de chat.
                        Cada bloco inclui os dois participantes e uma lista das mensagens mais recentes trocadas entre eles.
                        Requer autenticação via token JWT.
                    """,
            security = @SecurityRequirement(name = "bearerAuth") // Altere o nome caso tenha usado outro esquema no SwaggerConfig
    )
    public ResponseEntity<List<ChatConversationDTO>> getAllChatsForUser(@RequestParam(defaultValue = "0") int limit ) {
        return ResponseEntity.ok(chatService.getChatConversationsForUser(limit));
    }

}
