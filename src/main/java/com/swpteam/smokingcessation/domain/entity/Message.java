package com.swpteam.smokingcessation.domain.entity;


import com.swpteam.smokingcessation.common.AuditableEntity;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message extends AuditableEntity {

    String content;
}
