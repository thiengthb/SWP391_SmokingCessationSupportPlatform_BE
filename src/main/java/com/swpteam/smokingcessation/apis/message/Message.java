package com.swpteam.smokingcessation.apis.message;


import com.swpteam.smokingcessation.common.BaseEntity;
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
public class Message extends BaseEntity {
    @Column(unique = true)
    String content;
}
