package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
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

    @Builder.Default
    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<AITokenUsage> aiTokenUsages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Blog> blogs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Booking> bookings = new ArrayList<>();

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    Coach coach;

    @Builder.Default
    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Plan> plans = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Record> records = new ArrayList<>();

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, optional = false)
    @JsonManagedReference
    Setting setting;

    @Builder.Default
    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Subscription> subscriptions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "account")
    @JsonManagedReference
    List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    List<Notification> notifications;

    public boolean isHavingSubscription() {
        return subscriptions != null &&
                subscriptions.stream().anyMatch(Subscription::isActive);
    }
}