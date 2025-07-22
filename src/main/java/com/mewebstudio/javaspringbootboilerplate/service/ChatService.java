package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.dto.response.ChatMessageDTO;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.ChatConversationDTO;
import com.mewebstudio.javaspringbootboilerplate.dto.ws.ParticipanteDTO;
import com.mewebstudio.javaspringbootboilerplate.entity.ChatMessage;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.repository.ChatMessageRepository;
import com.mewebstudio.javaspringbootboilerplate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public Page<ChatMessageDTO> getConversation(String from, String to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<ChatMessage> pageResult = chatMessageRepository.findByFromUserOrToUser(UUID.fromString(from), UUID.fromString(to), pageable);
        return pageResult.map(this::mapToDTO);
    }

    public List<ChatConversationDTO> getChatConversationsForUser(int limit) {
        User autor = userService.getUser();

        List<ChatMessage> allMessages = chatMessageRepository.findAllMessagesForUser(autor.getId());

        Map<Set<UUID>, List<ChatMessageDTO>> grouped = new LinkedHashMap<>();

        for (ChatMessage message : allMessages) {
            UUID otherUserId = message.getFromUser().equals(autor.getId()) ? message.getToUser() : message.getFromUser();
            Set<UUID> key = new LinkedHashSet<>(Arrays.asList(autor.getId(), otherUserId));

            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(mapToDTO(message));
        }

        return grouped.entrySet().stream()
                .map(entry -> {
                    List<ChatMessageDTO> msgs = entry.getValue();

                    // Aplica limite se > 0
                    if (limit > 0 && msgs.size() > limit) {
                        msgs = msgs.subList(0, limit);
                    }

                    List<ParticipanteDTO> participantes = entry.getKey().stream()
                            .map(userId -> {
                                User user = userRepository.findById(userId)
                                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));
                                return new ParticipanteDTO(user.getId().toString(), user.getName());
                            })
                            .collect(Collectors.toList());

                    return new ChatConversationDTO(participantes, msgs);
                })
                .collect(Collectors.toList());
    }

    private ChatMessageDTO mapToDTO(ChatMessage message) {
        User from = userRepository.findById(message.getFromUser()).orElseThrow();
        User to = userRepository.findById(message.getToUser()).orElseThrow();

        return ChatMessageDTO.builder()
                .id(message.getId())
                .fromUserId(from.getId())
                .fromUserName(from.getName())
                .toUserId(to.getId())
                .toUserName(to.getName())
                .content(message.getContent())
                .type(message.getType())
                .date(message.getSentAt().withOffsetSameInstant(ZoneOffset.ofHours(-3)))
                .build();
    }
}
