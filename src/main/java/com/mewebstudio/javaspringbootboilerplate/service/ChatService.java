package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.dto.response.ChatMessageDTO;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public Page<ChatMessageDTO> getConversation(String from, String to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<ChatMessage> pageResult = chatMessageRepository.findByFromUserOrToUser(UUID.fromString(from), UUID.fromString(to), pageable);
        return pageResult.map(this::mapToDTO);
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
