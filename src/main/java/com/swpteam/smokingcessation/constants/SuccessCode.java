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

    ;

    int code;
    String message;
}
