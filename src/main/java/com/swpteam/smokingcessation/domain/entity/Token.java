package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.domain.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {

    @Id
    String id;

    @OneToOne
    @JoinColumn(name = "accountId", updatable = false, nullable = false)
    Account account;

    @Enumerated(EnumType.STRING)
    TokenType tokenType;

    LocalDateTime expiryTime;
}