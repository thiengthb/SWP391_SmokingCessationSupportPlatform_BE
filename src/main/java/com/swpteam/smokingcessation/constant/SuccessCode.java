package com.swpteam.smokingcessation.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SuccessCode {

    // General Successes (1000-1099)
    IMAGE_UPLOADED(1000, "success.general.image_uploaded"),
    MAIL_SENT(1001, "success.general.mail_sent"),

    // Account Successes (1100-1199)
    ACCOUNT_CREATED(1100, "success.account.created"),
    ACCOUNT_UPDATED(1101, "success.account.updated"),
    ACCOUNT_DELETED(1102, "success.account.deleted"),
    ACCOUNT_BANNED(1103, "success.account.banned"),
    PASSWORD_CHANGED(1104, "success.account.password_changed"),
    ROLE_UPDATED(1105, "success.account.role_updated"),
    ACCOUNT_FETCHED(1106, "success.account.fetched"),
    ACCOUNT_VERIFIED(1107, "success.account.verified"),

    // Authentication Successes (1200-1299)
    GOOGLE_LOGIN_SUCCESS(1200, "success.auth.google_login"),
    LOGIN_SUCCESS(1201, "success.auth.login"),
    REGISTER_SUCCESS(1202, "success.auth.register"),
    PASSWORD_RESET_SUCCESS(1203, "success.auth.password_reset"),
    TOKEN_REFRESH_SUCCESS(1204, "success.auth.token_refresh"),
    LOGOUT_SUCCESS(1205, "success.auth.logout"),

    // Membership Successes (1300-1399)
    MEMBERSHIP_CREATED(1300, "success.membership.created"),
    MEMBERSHIP_UPDATED(1301, "success.membership.updated"),
    MEMBERSHIP_DELETED(1302, "success.membership.deleted"),
    MEMBERSHIP_LIST_FETCHED(1303, "success.membership.list_fetched"),
    MEMBERSHIP_FETCHED_BY_ID(1304, "success.membership.fetched_by_id"),
    MEMBER_FETCHED_BY_FULLNAME(1305,"success.membership.fetchedByFullName"),

    // Subscription Successes (1400-1499)
    SUBSCRIPTION_CREATED(1400, "success.subscription.created"),
    SUBSCRIPTION_UPDATED(1401, "success.subscription.updated"),
    SUBSCRIPTION_DELETED(1402, "success.subscription.deleted"),
    SUBSCRIPTION_PAGE_FETCHED(1403, "success.subscription.page_fetched"),
    SUBSCRIPTION_FETCHED_BY_ID(1404, "success.subscription.fetched_by_id"),
    SUBSCRIPTION_FETCHED_BY_ACCOUNT(1405, "success.subscription.fetched_by_account"),

    // Health Record Successes (1500-1599)
    HEALTH_CREATED(1500, "success.health_record.created"),
    HEALTH_UPDATED(1501, "success.health_record.updated"),
    HEALTH_DELETED(1502, "success.health_record.deleted"),
    HEALTH_PAGE_FETCHED(1503, "success.health_record.page_fetched"),
    HEALTH_FETCHED_BY_ID(1504, "success.health_record.fetched_by_id"),
    HEALTH_FETCHED_BY_ACCOUNT(1505, "success.health_record.fetched_by_account"),

    // Record Successes (1600-1699)
    RECORD_CREATED(1600, "success.record.created"),
    RECORD_UPDATED(1601, "success.record.updated"),
    RECORD_DELETED(1602, "success.record.deleted"),
    RECORD_PAGE_FETCHED(1603, "success.record.page_fetched"),
    RECORD_FETCHED_BY_ID(1604, "success.record.fetched_by_id"),
    RECORD_FETCHED_BY_ACCOUNT(1605, "success.record.fetched_by_account"),

    // Message Successes (1700-1799)
    MESSAGE_CREATED(1700, "success.message.created"),
    MESSAGE_UPDATED(1701, "success.message.updated"),
    MESSAGE_DELETED(1702, "success.message.deleted"),
    MESSAGE_FETCHED_BY_ID(1703, "success.message.fetched_by_id"),
    MESSAGE_PAGE_FETCHED(1703, "success.message.page_fetched"),

    // Plan Successes (1800-1899)
    PLAN_CREATED(1800, "success.plan.created"),
    PLAN_UPDATED(1801, "success.plan.updated"),
    PLAN_DELETED(1802, "success.plan.deleted"),
    PLAN_FETCHED_BY_ID(1803, "success.plan.fetched_by_id"),
    PLAN_PAGE_FETCHED(1804, "success.plan.page_fetched"),
    PLAN_TEMPLATE_FETCHED(1805, "success.plan.template_fetched"),

    // Phase Successes (1900-1999)
    PHASE_CREATED(1900, "success.phase.created"),
    PHASE_UPDATED(1901, "success.phase.updated"),
    PHASE_DELETED(1902, "success.phase.deleted"),
    PHASE_FETCHED_BY_ID(1903, "success.phase.fetched_by_id"),
    PHASE_LIST_FETCHED(1904, "success.phase.list_fetched"),

    // Coach Successes (2000-2099)
    COACH_CREATED(2000, "success.coach.created"),
    COACH_UPDATED(2001, "success.coach.updated"),
    COACH_DELETED(2002, "success.coach.deleted"),
    COACH_FETCHED_BY_ID(2003, "success.coach.fetched_by_id"),
    COACH_PAGE_FETCHED(2004, "success.coach.page_fetched"),

    // Member Successes (2100-2199)
    MEMBER_CREATED(2100, "success.member.created"),
    MEMBER_UPDATED(2101, "success.member.updated"),
    MEMBER_FETCHED_BY_ID(2103, "success.member.fetched_by_id"),
    MEMBER_PROGRESS_FETCHED(2104, "success.progress.fetched"),

    // Booking Successes (2200-2299)
    BOOKING_CREATED(2200, "success.booking.created"),
    BOOKING_UPDATED(2201, "success.booking.updated"),
    BOOKING_DELETED(2202, "success.booking.deleted"),
    BOOKING_PAGE_FETCHED(2203, "success.booking.page_fetched"),
    BOOKING_FETCHED_BY_ID(2204, "success.booking.fetched_by_id"),
    BOOKING_ANSWERED(2205, "success.booking.answered"),

    // Chatbot Successes (2300-2399)
    CHATBOT_RESPONSE_RETURNED(2300, "success.chatbot.response_returned"),

    // Blog Successes (2400-2499)
    BLOG_CREATED(2400, "success.blog.created"),
    BLOG_UPDATED(2401, "success.blog.updated"),
    BLOG_DELETED(2402, "success.blog.deleted"),
    BLOG_PAGE_FETCHED(2403, "success.blog.page_fetched"),
    BLOG_FETCHED_BY_ID(2404, "success.blog.fetched_by_id"),
    BLOG_FETCHED_BY_SLUG(2405, "success.blog.fetched_by_slug"),
    BLOG_FETCHED_BY_CATEGORY(2406, "success.blog.fetched_by_category"),
    MY_BLOG_FETCHED(2407, "success.blog.my_blog_fetched"),

    // Category Successes (2500-2599)
    CATEGORY_CREATED(2500, "success.category.created"),
    CATEGORY_UPDATED(2501, "success.category.updated"),
    CATEGORY_DELETED(2502, "success.category.deleted"),
    CATEGORY_LIST_FETCHED(2503, "success.category.list_fetched"),
    CATEGORY_PAGE_FETCHED(2503, "success.category.page_fetched"),
    CATEGORY_FETCHED_BY_ID(2504, "success.category.fetched_by_id"),

    // Comment Successes (2600-2699)
    COMMENT_CREATED(2600, "success.comment.created"),
    COMMENT_UPDATED(2601, "success.comment.updated"),
    COMMENT_DELETED(2602, "success.comment.deleted"),
    COMMENT_PAGE_FETCHED(2603, "success.comment.page_fetched"),
    COMMENT_FETCHED_BY_ID(2604, "success.comment.fetched_by_id"),
    COMMENT_FETCHED_BY_BLOG(2605, "success.comment.fetched_by_blog"),

    // Notification Successes (2700-2799)
    NOTIFICATION_SENT(2700, "success.notification.sent"),
    NOTIFICATION_MARKED_READ(2701, "success.notification.marked_read"),
    NOTIFICATION_PAGE_FETCHED(2702, "success.notification.page_fetched"),
    NOTIFICATION_FETCHED_BY_ID(2703, "success.notification.fetched_by_id"),
    NOTIFICATION_DELETED(2704, "success.notification.deleted"),
    NOTIFICATION_ALL_DELETED(2705, "success.notification.all_deleted"),

    // Chat Successes (2800-2899)
    CHAT_PAGE_FETCHED(2800, "success.chat.page_fetched"),
    CHAT_FETCHED_BY_ID(2801, "success.chat.fetched_by_id"),
    CHAT_DELETED(2802, "success.chat.deleted"),

    // Streak Successes (2900-2999)
    STREAK_CREATED(2900, "success.streak.created"),
    STREAK_UPDATED(2901, "success.streak.updated"),
    STREAK_DELETED(2902, "success.streak.deleted"),
    STREAK_PAGE_FETCHED(2903, "success.streak.page_fetched"),
    STREAK_FETCHED_BY_ID(2904, "success.streak.fetched_by_id"),
    STREAK_RESET(2905, "success.streak.reset"),

    // Report Successes (3000-3099)
    REPORT_SUMMARY_FETCHED(3000, "success.report.summary_fetched"),
    USER_DISTRIBUTION_FETCHED(3001, "success.report.user_distribution_fetched"),
    USER_GROWTH_FETCHED(3002, "success.report.user_growth_fetched"),
    REVENUE_FETCHED(3003, "success.report.revenue_fetched"),
    PREMIUM_DISTRIBUTION_FETCHED(3003, "success.report.premium_distribution.fetched"),

    // Goal Successes (3100-3199)
    GOAL_CREATED(3100, "success.goal.created"),
    GOAL_UPDATED(3101, "success.goal.updated"),
    GOAL_DELETED(3102, "success.goal.deleted"),
    GOAL_PAGE_FETCHED(3103, "success.goal.page_fetched"),
    GOAL_FETCHED_BY_NAME(3104, "success.goal.fetched_by_name"),

    // Feedback Successes (3200-3299)
    FEEDBACK_CREATED(3200, "success.feedback.created"),
    FEEDBACK_UPDATED(3201, "success.feedback.updated"),
    FEEDBACK_DELETED(3202, "success.feedback.deleted"),
    FEEDBACK_PAGE_FETCHED(3203, "success.feedback.page_fetched"),
    FEEDBACK_FETCHED_BY_ID(3204, "success.feedback.fetched_by_id"),
    FEEDBACK_FETCHED_BY_ACCOUNT(3205, "success.feedback.fetched_by_account"),

    // Review Successes (3300-3399)
    REVIEW_CREATED(3300, "success.review.created"),
    REVIEW_UPDATED(3301, "success.review.updated"),
    REVIEW_DELETED(3302, "success.review.deleted"),
    REVIEW_PAGE_FETCHED(3303, "success.review.page_fetched"),
    REVIEW_FETCHED_BY_ID(3304, "success.review.fetched_by_id"),
    REVIEW_FETCHED_BY_ACCOUNT(3305, "success.review.fetched_by_account"),
    REVIEW_FETCHED_BY_COACH(3306, "success.review.fetched_by_coach"),

    // Timetable Successes (3400-3499)
    TIMETABLE_CREATED(3400, "success.timetable.created"),
    TIMETABLE_PAGE_FETCHED(3401, "success.timetable.page_fetched"),
    TIMETABLE_FETCHED_BY_ID(3402, "success.timetable.fetched_by_id"),
    TIMETABLE_UPDATED(3403,"success.timetable.updated"),
    TIMETABLE_DELETED(3404, "success.timetable.deleted"),

    // Setting Successes (3500-3599)
    SETTING_UPDATED(3500, "success.setting.updated"),
    SETTING_FETCHED(3501, "success.setting.fetched"),

    // Stripe Successes (3600-3699)
    CHECKOUT_SUCCESS(3600, "success.stripe.checkout"),

    // Counter Success (3700-3799)
    COUNTER_STARTED(3700, "success.counter.started"),
    COUNTER_FETCHED(3701, "success.counter.fetched"),

    // Contact Success (3800-3899)
    CONTACT_SUCCESS(3801,"success.contact.success"),

    // Transaction Success (3900-3999)
    TRANSACTION_FETCH_BY_ACCOUNT(3901, "success.transaction.fetch_by_account"),
    TRANSACTION_FETCH_BY_ID(3901, "success.transaction.fetch_by_id"),

    // Statistics (4000 - 4099)
    STATISTICS_FETCHED_BY_ACCOUNT(4000, "success.statistics.fetch_by_account"),
    ;
    int code;
    String messageLocaleKey;
}
