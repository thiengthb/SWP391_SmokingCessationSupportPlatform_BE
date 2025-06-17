package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.swpteam.smokingcessation.utils.DateTimeUtil.DATE_TIME_FORMAT_MILLISECOND;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    double revenue;

    int newAccounts;

    int currentAccounts;

    int activeAccounts;

    @CreatedDate
    @Column(updatable = false)
    @JsonFormat(pattern = DATE_TIME_FORMAT_MILLISECOND)
    LocalDateTime createdAt;
}
