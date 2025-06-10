package com.swpteam.smokingcessation.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SuccessCode {

    // Membership
    MEMBERSHIP_CREATED(1000, "Membership has been created"),
    MEMBERSHIP_UPDATED(1001, "Membership has been updated"),
    MEMBERSHIP_DELETED(1002, "Membership has been deleted"),

    // Account
    ACCOUNT_CREATED(3000, "Account has been created"),
    ACCOUNT_UPDATED(3001, "Account has been updated"),
    ACCOUNT_DELETED(3002, "Account has been deleted"),
    PASSWORD_CHANGE_SUCCESS(3003, "Password is changed successfully"),

    // Member
    MEMBER_CREATED(3000, "Member has been created"),
    MEMBER_UPDATED(3001, "Member has been updated"),
    ;

    int code;
    String message;
}
