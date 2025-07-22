package com.mewebstudio.javaspringbootboilerplate.repository;

import com.mewebstudio.javaspringbootboilerplate.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("""
                SELECT m FROM ChatMessage m
                WHERE (m.fromUser = :user1 AND m.toUser = :user2)
                   OR (m.fromUser = :user2 AND m.toUser = :user1)
                ORDER BY m.sentAt DESC
            """)
    Page<ChatMessage> findByFromUserOrToUser(@Param("user1") UUID fromUser, @Param("user2") UUID toUser, Pageable pageable);

    @Query("""
                SELECT m FROM ChatMessage m
                JOIN User fromUser ON fromUser.id = m.fromUser
                JOIN User toUser ON toUser.id = m.toUser
                WHERE m.fromUser = :userId OR m.toUser = :userId
                ORDER BY m.sentAt DESC
            """)
    List<ChatMessage> findAllMessagesForUser(@Param("userId") UUID userId);
}
