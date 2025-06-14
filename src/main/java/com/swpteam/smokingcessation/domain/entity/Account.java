package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends BaseEntity {

    @Column(unique = true, columnDefinition = "NVARCHAR(30)")
    String username;

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(30)")
    String email;

    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    @Column(unique = true, columnDefinition = "NVARCHAR(10)")
    String phoneNumber;

    @Enumerated(EnumType.STRING)
    Role role;

    @Enumerated(EnumType.STRING)
    AccountStatus status;

    String avatar;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    Member member;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, optional = false)
    @JsonManagedReference
    Setting setting;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<AITokenUsage> aiTokenUsages;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Blog> blogs;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Comment> comments;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Record> records;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Subscription> subscriptions;

    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Transaction> transactions;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    Coach coach;

    @OneToMany(mappedBy = "account")
    List<Plan> plans;

    @OneToMany(mappedBy = "account")
    List<Booking> bookings;

    @OneToMany(mappedBy = "account")
    List<Notification> notifications;

    public boolean isHavingSubscription() {
        return subscriptions != null &&
                subscriptions.stream().anyMatch(Subscription::isActive);
    }
}