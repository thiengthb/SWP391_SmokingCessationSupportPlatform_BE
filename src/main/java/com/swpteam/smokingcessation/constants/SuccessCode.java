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
    MEMBERSHIP_GET_ALL(1003, "Success returning a page of membership"),
    MEMBERSHIP_GET_BY_ID(1004, "Success returning a membership with given id"),

    // Subscription
    SUBSCRIPTION_CREATED(1000, "Subscription has been created"),
    SUBSCRIPTION_UPDATED(1001, "Subscription has been updated"),
    SUBSCRIPTION_DELETED(1002, "Subscription has been deleted"),
    SUBSCRIPTION_GET_ALL(1003, "Success returning a page of subscription"),
    SUBSCRIPTION_GET_BY_ID(1004, "Success returning a subscription with given id"),

    //Message
    MESSAGE_CREATED(2000, "Message has been created"),
    MESSAGE_UPDATED(2001, "Message has been updated"),
    MESSAGE_DELETED(2002, "Message has been deleted"),
    MESSAGE_GET_BY_ID(2003, "Success returning a membership with given id"),

    ;

    int code;
    String message;
}
