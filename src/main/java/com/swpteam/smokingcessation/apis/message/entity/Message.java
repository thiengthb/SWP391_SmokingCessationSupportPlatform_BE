package com.swpteam.smokingcessation.apis.message.entity;

import com.swpteam.smokingcessation.apis.message.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String messageId;
    @Enumerated(EnumType.STRING)
    MessageType type;
    @Column(unique = true)
    String content;
    @Column(updatable = false)
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean isDeleted;

}
