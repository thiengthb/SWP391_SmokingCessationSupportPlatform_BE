package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swpteam.smokingcessation.common.BaseEntity;
import com.swpteam.smokingcessation.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static com.swpteam.smokingcessation.utils.DateTimeUtil.DATE_TIME_FORMAT_MILLISECOND;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "accountId")
    Account account;

    String content;

    @Enumerated(EnumType.STRING)
    NotificationType notificationType;

    @JsonFormat(pattern = DATE_TIME_FORMAT_MILLISECOND)
    LocalDateTime sentAt;

    boolean isRead;
}
