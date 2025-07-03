package com.swpteam.smokingcessation.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    // General Errors (1000-1099)
    UNCATEGORIZED_ERROR(1000, "error.general.uncategorized", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST_BODY(1001, "error.general.invalid_request_body", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND(1002, "error.general.entity_not_found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1003, "error.general.unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1004, "error.general.unauthorized", HttpStatus.FORBIDDEN),
    INVALID_MESSAGE_KEY(1005, "error.general.invalid_message_key", HttpStatus.BAD_REQUEST),
    MESSAGE_ERROR(1006, "error.general.message", HttpStatus.BAD_REQUEST),
    INVALID_FIELD(1006, "error.general.invalid_field", HttpStatus.BAD_REQUEST),
    ROLE_NOT_SUPPORTED(1007, "error.general.role_not_supported", HttpStatus.FORBIDDEN),

    // Account Errors (1100-1199)
    ACCOUNT_REQUIRED(1100, "error.account.required", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1101, "error.account.email_required", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(1102, "error.account.invalid_email_format", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1103, "error.account.password_required", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1104, "error.account.invalid_password", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(1105, "error.account.phone_number_invalid", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND(1106, "error.account.not_found", HttpStatus.NOT_FOUND),
    ACCOUNT_ALREADY_EXISTS(1107, "error.account.already_exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(1108, "error.account.email_already_exists", HttpStatus.CONFLICT),
    PHONE_NUMBER_ALREADY_EXISTS(1109, "error.account.phone_number_already_exists", HttpStatus.CONFLICT),
    PASSWORD_MISMATCH(1110, "error.account.password_mismatch", HttpStatus.BAD_REQUEST),
    ACCOUNT_ALREADY_ACTIVATED(1111, "error.account.already_activated", HttpStatus.BAD_REQUEST),
    ROLE_REQUIRED(1112, "error.account.role_required", HttpStatus.BAD_REQUEST),
    SELF_BAN_DISALLOWED(1113, "error.account.self_ban_disallowed", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1114, "error.account.wrong_password", HttpStatus.BAD_REQUEST),
    ACCOUNT_NEED_ACTIVATE(1115, "error.account.need_activate", HttpStatus.FORBIDDEN),

    // Authentication & Token Errors (1200-1299)
    TOKEN_REQUIRED(1200, "error.token.required", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1201, "error.token.expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1202, "error.token.invalid", HttpStatus.BAD_REQUEST),
    TOKEN_CREATION_FAILED(1203, "error.token.creation_failed", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_NOT_FOUND(1204, "error.token.not_found", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_SAVABLE(1205, "error.token.not_savable", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TOKEN_SIGNATURE(1206, "error.token.invalid_signature", HttpStatus.BAD_REQUEST),
    USED_TOKEN(1207, "error.token.used", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_FAILED(1208, "error.token.email_send_failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Membership Errors (1300-1399)
    MEMBERSHIP_NOT_FOUND(1300, "error.membership.not_found", HttpStatus.NOT_FOUND),
    MEMBERSHIP_NAME_DUPLICATE(1301, "error.membership.name_duplicate", HttpStatus.CONFLICT),
    MEMBERSHIP_NAME_REQUIRED(1302, "error.membership.name_required", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_NAME_TOO_SHORT(1303, "error.membership.name_too_short", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_NAME_TOO_LONG(1304, "error.membership.name_too_long", HttpStatus.BAD_REQUEST),
    DURATION_INVALID(1305, "error.membership.duration_invalid", HttpStatus.BAD_REQUEST),
    PRICE_INVALID(1306, "error.membership.price_invalid", HttpStatus.BAD_REQUEST),
    DURATION_REQUIRED(1307, "error.membership.duration_required", HttpStatus.BAD_REQUEST),
    PRICE_REQUIRED(1308, "error.membership.price_required", HttpStatus.BAD_REQUEST),

    // Transaction Errors (1400-1499)
    TRANSACTION_NOT_FOUND(1400, "error.transaction.not_found", HttpStatus.NOT_FOUND),
    TRANSACTION_AMOUNT_REQUIRED(1401, "error.transaction.amount_required", HttpStatus.BAD_REQUEST),
    TRANSACTION_NAME_REQUIRED(1402, "error.transaction.name_required", HttpStatus.BAD_REQUEST),
    CURRENCY_REQUIRED(1403, "error.transaction.currency_required", HttpStatus.BAD_REQUEST),
    TRANSACTION_AMOUNT_INVALID(1404, "error.transaction.amount_invalid", HttpStatus.BAD_REQUEST),

    // Subscription Errors (1500-1599)
    SUBSCRIPTION_NOT_FOUND(1500, "error.subscription.not_found", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_START_DATE_INVALID(1501, "error.subscription.start_date_invalid", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_END_DATE_INVALID(1502, "error.subscription.end_date_invalid", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_START_DATE_REQUIRED(1501, "error.subscription.start_date_required", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_END_DATE_REQUIRED(1501, "error.subscription.end_date_required", HttpStatus.BAD_REQUEST),

    // Settings Errors (1600-1699)
    THEME_REQUIRED(1600, "error.settings.theme_required", HttpStatus.BAD_REQUEST),
    LANGUAGE_REQUIRED(1601, "error.settings.language_required", HttpStatus.BAD_REQUEST),
    TRACKING_MODE_REQUIRED(1602, "error.settings.tracking_mode_required", HttpStatus.BAD_REQUEST),
    MOTIVATION_REQUIRED(1603, "error.settings.motivation_required", HttpStatus.BAD_REQUEST),
    REPORT_DEADLINE_REQUIRED(1606, "error.settings.report_deadline_required", HttpStatus.BAD_REQUEST),
    MODE_CHANGE_UNAVAILABLE(1607,"error.settings.mode_change_unavailable", HttpStatus.BAD_REQUEST),

    // Member Errors (1700-1799)
    MEMBER_ALREADY_EXISTS(1700, "error.member.already_exists", HttpStatus.CONFLICT),
    MEMBER_NOT_FOUND(1701, "error.member.not_found", HttpStatus.NOT_FOUND),
    INVALID_DOB(1702, "error.member.invalid_dob", HttpStatus.BAD_REQUEST),
    GENDER_REQUIRED(1703, "error.member.gender_required", HttpStatus.BAD_REQUEST),

    // Message Errors (1800-1899)
    MESSAGE_NOT_FOUND(1800, "error.message.not_found", HttpStatus.NOT_FOUND),
    MESSAGE_CONTENT_REQUIRED(1801, "error.message.content_required", HttpStatus.BAD_REQUEST),

    // Health Record Errors (1900-1999)
    HEALTH_RECORD_NOT_FOUND(1900, "error.health_record.not_found", HttpStatus.NOT_FOUND),
    CIGARETTES_PER_DAY_INVALID(1901, "error.health_record.cigarettes_per_day_invalid", HttpStatus.BAD_REQUEST),
    CIGARETTES_PER_PACK_INVALID(1902, "error.health_record.cigarettes_per_pack_invalid", HttpStatus.BAD_REQUEST),
    FTND_LEVEL_INVALID_MIN(1903, "error.health_record.ftnd_level_invalid_min", HttpStatus.BAD_REQUEST),
    FTND_LEVEL_INVALID_MAX(1904, "error.health_record.ftnd_level_invalid_max", HttpStatus.BAD_REQUEST),
    PACK_PRICE_INVALID(1905, "error.health_record.pack_price_invalid", HttpStatus.BAD_REQUEST),
    PACK_PRICE_TOO_HIGH(1906, "error.health_record.pack_price_too_high", HttpStatus.BAD_REQUEST),
    REASON_TO_QUIT_REQUIRED(1907, "error.health_record.reason_to_quit_required", HttpStatus.BAD_REQUEST),
    REASON_TO_QUIT_TOO_LONG(1908, "error.health_record.reason_to_quit_too_long", HttpStatus.BAD_REQUEST),
    SMOKE_YEAR_INVALID(1909, "error.health_record.smoke_year_invalid", HttpStatus.BAD_REQUEST),
    SMOKE_YEAR_TOO_HIGH(1910, "error.health_record.smoke_year_too_high", HttpStatus.BAD_REQUEST),
    HEALTH_RECORD_UPDATE_FAILED(1911, "error.health_record.update_failed", HttpStatus.BAD_REQUEST),

    // Record Errors (2000-2099)
    RECORD_NOT_FOUND(2000, "error.record.not_found", HttpStatus.NOT_FOUND),
    CIGARETTES_SMOKED_INVALID(2001, "error.record.cigarettes_smoked_invalid", HttpStatus.BAD_REQUEST),
    RECORD_DATE_REQUIRED(2002, "error.record.date_required", HttpStatus.BAD_REQUEST),
    RECORD_DATE_INVALID(2003, "error.record.date_invalid", HttpStatus.BAD_REQUEST),
    RECORD_ALREADY_EXISTS(2004, "error.record.already_exists", HttpStatus.CONFLICT),

    // Currency Errors (2100-2199)
    CURRENCY_RATE_UPDATE_FAILED(2100, "error.currency.rate_update_failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_CURRENCY(2101, "error.currency.invalid", HttpStatus.BAD_REQUEST),

    // Plan Errors (2200-2299)
    PLAN_NOT_FOUND(2200, "error.plan.not_found", HttpStatus.NOT_FOUND),
    NO_PLAN_CREATED(2201, "error.plan.no_plan_created", HttpStatus.NOT_FOUND),
    PLAN_START_DATE_INVALID(2202, "error.plan.start_date_invalid", HttpStatus.BAD_REQUEST),
    PLAN_END_DATE_INVALID(2203, "error.plan.end_date_invalid", HttpStatus.BAD_REQUEST),
    PLAN_START_DATE_REQUIRED(2204, "error.plan.start_date_required", HttpStatus.BAD_REQUEST),
    PLAN_END_DATE_REQUIRED(2205, "error.plan.end_date_required", HttpStatus.BAD_REQUEST),
    PLAN_STATUS_REQUIRED(2206, "error.plan.status_required", HttpStatus.BAD_REQUEST),
    PLAN_NAME_REQUIRED(2207, "error.plan.name_required", HttpStatus.BAD_REQUEST),
    PLAN_NAME_TOO_LONG(2208, "error.plan.name_too_long", HttpStatus.BAD_REQUEST),
    PLAN_DESCRIPTION_REQUIRED(2209, "error.plan.description_required", HttpStatus.BAD_REQUEST),
    PLAN_DESCRIPTION_TOO_LONG(2210, "error.plan.description_too_long", HttpStatus.BAD_REQUEST),
    SUCCESS_RATE_REQUIRED(2211, "error.plan.success_rate_required", HttpStatus.BAD_REQUEST),
    SUCCESS_RATE_INVALID_MIN(2212, "error.plan.success_rate_invalid_min", HttpStatus.BAD_REQUEST),
    SUCCESS_RATE_INVALID_MAX(2213, "error.plan.success_rate_invalid_max", HttpStatus.BAD_REQUEST),
    PLAN_ID_REQUIRED(2214, "error.plan.id_required", HttpStatus.BAD_REQUEST),
    FTND_SCORE_INVALID(2215, "error.plan.ftnd_score_invalid", HttpStatus.BAD_REQUEST),
    PLAN_ALREADY_EXISTED(2216, "error.plan.already_existed", HttpStatus.CONFLICT),
    INVALID_PLAN_DURATION(2217, "error.plan.invalid_duration", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_PLAN_NOT_PENDING(2218,"error.plan.invalid_plan_status",HttpStatus.BAD_REQUEST),
    PLAN_ALREADY_EXISTED_A(2219, "error.plan.already_existed_a", HttpStatus.BAD_REQUEST),
    PLAN_ALREADY_EXISTED_B(2220, "error.plan.already_existed_b", HttpStatus.BAD_REQUEST),
    RESTRICT_PLAN_A(2221, "error.plan.restrict_plan_a", HttpStatus.BAD_REQUEST),
    RESTRICT_PLAN_B(2222, "error.plan.restrict_plan_b", HttpStatus.BAD_REQUEST),


    // Phase Errors (2300-2399)
    PHASE_NOT_FOUND(2300, "error.phase.not_found", HttpStatus.NOT_FOUND),
    PHASE_START_DATE_REQUIRED(2301, "error.phase.start_date_required", HttpStatus.BAD_REQUEST),
    PHASE_END_DATE_REQUIRED(2302, "error.phase.end_date_required", HttpStatus.BAD_REQUEST),
    PHASE_START_DATE_INVALID(2303, "error.phase.start_date_invalid", HttpStatus.BAD_REQUEST),
    PHASE_END_DATE_INVALID(2304, "error.phase.end_date_invalid", HttpStatus.BAD_REQUEST),
    PHASE_STATUS_REQUIRED(2305, "error.phase.status_required", HttpStatus.BAD_REQUEST),
    PHASE_NAME_TOO_LONG(2305, "error.phase.name_too_long", HttpStatus.BAD_REQUEST),
    PHASE_DESCRIPTION_TOO_LONG(2307, "error.phase.description_too_long", HttpStatus.BAD_REQUEST),
    PHASE_CIGARETTE_BOUND_REQUIRED(2308, "error.phase.cigarette_bound_required", HttpStatus.BAD_REQUEST),
    CIGARETTE_BOUND_INVALID(2309, "error.phase.cigarette_bound_invalid", HttpStatus.BAD_REQUEST),
    PHASE_OVERLAP(2310, "error.phase.overlap", HttpStatus.BAD_REQUEST),
    PHASE_REQUIRED(2311, "error.phase.required", HttpStatus.BAD_REQUEST),
    PHASE_DURATION_TOO_SHORT(2312, "error.phase.duration_too_short", HttpStatus.BAD_REQUEST),
    INVALID_PHASE_DATE(2313, "error.phase.invalid_date", HttpStatus.BAD_REQUEST),
    NEW_PHASE_CONFLICT(2314, "error.phase.new_phase_conflict", HttpStatus.CONFLICT),
    NO_COMPLETED_PHASE_FOUND(2315,"error.phase.no_completed_phase_found",HttpStatus.BAD_REQUEST),

    // Chatbot Errors (2400-2499)
    PROMPT_REQUIRED(2400, "error.chatbot.prompt_required", HttpStatus.BAD_REQUEST),
    PROMPT_TOO_LONG(2401, "error.chatbot.prompt_too_long", HttpStatus.BAD_REQUEST),
    REQUEST_LIMIT_EXCEEDED(2402, "error.chatbot.request_limit_exceeded", HttpStatus.TOO_MANY_REQUESTS),

    // Coach Errors (2500-2599)
    COACH_NOT_FOUND(2500, "error.coach.not_found", HttpStatus.NOT_FOUND),
    COACH_FULL_NAME_REQUIRED(2500, "error.coach.full_name_required", HttpStatus.BAD_REQUEST),
    COACH_BIO_TOO_LONG(2501, "error.coach.bio_too_long", HttpStatus.BAD_REQUEST),
    COACH_SOCIAL_LINK_REQUIRED(2502, "error.coach.social_link_required", HttpStatus.BAD_REQUEST),
    COACH_EXPERIENCE_YEARS_REQUIRED(2502, "error.coach.experience_years_required", HttpStatus.BAD_REQUEST),
    COACH_SPECIALIZATION_REQUIRED(2503, "error.coach.specialization_required", HttpStatus.BAD_REQUEST),
    COACH_CERTIFICATES_REQUIRED(2504, "error.coach.certificates_required", HttpStatus.BAD_REQUEST),
    COACH_ALREADY_EXISTS(2505, "error.coach.already_exists", HttpStatus.CONFLICT),

    // Booking Errors (2600-2699)
    BOOKING_NOT_FOUND(2600, "error.booking.not_found", HttpStatus.NOT_FOUND),
    COACH_ACCOUNT_ID_REQUIRED(2601, "error.booking.coach_account_id_required", HttpStatus.BAD_REQUEST),
    MEET_LINK_REQUIRED(2602, "error.booking.meet_link_required", HttpStatus.BAD_REQUEST),
    MEET_LINK_TOO_LONG(2603, "error.booking.meet_link_too_long", HttpStatus.BAD_REQUEST),
    BOOKING_START_DATE_REQUIRED(2604, "error.booking.start_date_required", HttpStatus.BAD_REQUEST),
    BOOKING_START_DATE_INVALID(2605, "error.booking.start_date_invalid", HttpStatus.BAD_REQUEST),
    BOOKING_END_DATE_REQUIRED(2606, "error.booking.end_date_required", HttpStatus.BAD_REQUEST),
    BOOKING_END_DATE_INVALID(2607, "error.booking.end_date_invalid", HttpStatus.BAD_REQUEST),
    BOOKING_APPROVAL_REQUIRED(2608, "error.booking.approval_required", HttpStatus.BAD_REQUEST),
    BOOKING_OUTSIDE_WORKING_HOURS(2609, "error.booking.outside_working_hours", HttpStatus.BAD_REQUEST),
    BOOKING_TIME_CONFLICT(2610, "error.booking.time_conflict", HttpStatus.CONFLICT),
    BOOKING_ALREADY_IN_PROCESS(2611, "error.booking.already_in_process", HttpStatus.CONFLICT),

    // Category Errors (2700-2799)
    CATEGORY_NAME_REQUIRED(2700, "error.category.name_required", HttpStatus.BAD_REQUEST),
    CATEGORY_ALREADY_EXISTS(2701, "error.category.already_exists", HttpStatus.CONFLICT),
    CATEGORY_NAME_TOO_LONG(2702, "error.category.name_too_long", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(2703, "error.category.not_found", HttpStatus.NOT_FOUND),
    CATEGORY_DELETION_NOT_ALLOWED(2704, "error.category.deletion_not_allowed", HttpStatus.BAD_REQUEST),

    // Comment Errors (2800-2899)
    COMMENT_CONTENT_REQUIRED(2800, "error.comment.content_required", HttpStatus.BAD_REQUEST),
    COMMENT_REPLY_REQUIRED(2801, "error.comment.reply_required", HttpStatus.BAD_REQUEST),
    COMMENT_LEVEL_INVALID(2802, "error.comment.level_invalid", HttpStatus.BAD_REQUEST),
    COMMENT_BLOG_ID_REQUIRED(2803, "error.comment.blog_id_required", HttpStatus.BAD_REQUEST),
    COMMENT_USER_ID_REQUIRED(2804, "error.comment.user_id_required", HttpStatus.BAD_REQUEST),
    COMMENT_EDIT_NOT_ALLOWED(2805, "error.comment.edit_not_allowed", HttpStatus.FORBIDDEN),
    COMMENT_NOT_FOUND(2806, "error.comment.not_found", HttpStatus.NOT_FOUND),

    // Blog Errors (2900-2999)
    BLOG_TITLE_REQUIRED(2900, "error.blog.title_required", HttpStatus.BAD_REQUEST),
    BLOG_TITLE_TOO_LONG(2901, "error.blog.title_too_long", HttpStatus.BAD_REQUEST),
    BLOG_SLUG_REQUIRED(2902, "error.blog.slug_required", HttpStatus.BAD_REQUEST),
    BLOG_SLUG_INVALID(2903, "error.blog.slug_invalid", HttpStatus.BAD_REQUEST),
    BLOG_SLUG_TOO_LONG(2904, "error.blog.slug_too_long", HttpStatus.BAD_REQUEST),
    BLOG_COVER_IMAGE_TOO_LONG(2905, "error.blog.cover_image_too_long", HttpStatus.BAD_REQUEST),
    BLOG_COVER_IMAGE_INVALID(2906, "error.blog.cover_image_invalid", HttpStatus.BAD_REQUEST),
    BLOG_EXCERPT_TOO_LONG(2907, "error.blog.excerpt_too_long", HttpStatus.BAD_REQUEST),
    BLOG_STATUS_REQUIRED(2908, "error.blog.status_required", HttpStatus.BAD_REQUEST),
    BLOG_NOT_FOUND(2909, "error.blog.not_found", HttpStatus.NOT_FOUND),

    // Google Calendar Errors (3000-3099)
    GOOGLE_CALENDAR_ERROR(3000, "error.google_calendar.error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Chat Errors (3100-3199)
    CHAT_MESSAGE_REQUIRED(3100, "error.chat.message_required", HttpStatus.BAD_REQUEST),
    CHAT_MESSAGE_TOO_LONG(3101, "error.chat.message_too_long", HttpStatus.BAD_REQUEST),
    CHAT_DELETION_NOT_ALLOWED(3102, "error.chat.deletion_not_allowed", HttpStatus.FORBIDDEN),
    CHAT_NOT_FOUND(3103, "error.chat.not_found", HttpStatus.NOT_FOUND),

    // Notification Errors (3200-3299)
    NOTIFICATION_DELETION_NOT_ALLOWED(3200, "error.notification.deletion_not_allowed", HttpStatus.FORBIDDEN),
    NOTIFICATION_CONTENT_REQUIRED(3201, "error.notification.content_required", HttpStatus.BAD_REQUEST),
    NOTIFICATION_ID_REQUIRED(3202, "error.notification.id_required", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_FOUND(3203, "error.notification.not_found", HttpStatus.NOT_FOUND),
    NOTIFICATION_ALREADY_READ(3204, "error.notification.already_read", HttpStatus.BAD_REQUEST),

    // Streak Errors (3300-3399)
    STREAK_RESET_FAILED(3300, "error.streak.reset_failed", HttpStatus.INTERNAL_SERVER_ERROR),
    STREAK_NOT_FOUND(3301, "error.streak.not_found", HttpStatus.NOT_FOUND),
    STREAK_INVALID(3302, "error.streak.invalid", HttpStatus.BAD_REQUEST),
    STREAK_REQUIRED(3303, "error.streak.required", HttpStatus.BAD_REQUEST),
    STREAK_DELETION_NOT_ALLOWED(3304, "error.streak.deletion_not_allowed", HttpStatus.FORBIDDEN),
    STREAK_ALREADY_EXISTS(3305, "error.streak.already_exists", HttpStatus.CONFLICT),
    STREAK_DOWNGRADE_NOT_ALLOWED(3306, "error.streak.downgrade_not_allowed", HttpStatus.BAD_REQUEST),

    // Report Errors (3400-3499)
    REPORT_FROM_DATE_INVALID(3400, "error.report.from_date_invalid", HttpStatus.BAD_REQUEST),
    REPORT_TO_DATE_INVALID(3401, "error.report.to_date_invalid", HttpStatus.BAD_REQUEST),
    REPORT_FROM_DATE_REQUIRED(3402, "error.report.from_date_required", HttpStatus.BAD_REQUEST),

    // Goal Errors (3500-3599)
    GOAL_NOT_FOUND(3500, "error.goal.not_found", HttpStatus.NOT_FOUND),
    GOAL_ALREADY_EXISTS(3501, "error.goal.already_exists", HttpStatus.CONFLICT),
    GOAL_NAME_REQUIRED(3502, "error.goal.name_required", HttpStatus.BAD_REQUEST),
    GOAL_DESCRIPTION_REQUIRED(3503, "error.goal.description_required", HttpStatus.BAD_REQUEST),
    GOAL_ICON_URL_REQUIRED(3504, "error.goal.icon_url_required", HttpStatus.BAD_REQUEST),
    GOAL_CRITERIA_TYPE_REQUIRED(3505, "error.goal.criteria_type_required", HttpStatus.BAD_REQUEST),
    GOAL_CRITERIA_VALUE_REQUIRED(3506, "error.goal.criteria_value_required", HttpStatus.BAD_REQUEST),
    GOAL_CRITERIA_VALUE_INVALID(3507, "error.goal.criteria_value_invalid", HttpStatus.BAD_REQUEST),

    // Feedback Errors (3600-3699)
    FEEDBACK_NOT_FOUND(3600, "error.feedback.not_found", HttpStatus.NOT_FOUND),
    FEEDBACK_COMMENT_REQUIRED(3601, "error.feedback.comment_required", HttpStatus.BAD_REQUEST),
    FEEDBACK_RATING_INVALID_MIN(3602, "error.feedback.rating_invalid_min", HttpStatus.BAD_REQUEST),
    FEEDBACK_RATING_INVALID_MAX(3603, "error.feedback.rating_invalid_max", HttpStatus.BAD_REQUEST),

    // Review Errors (3700-3799)
    REVIEW_NOT_FOUND(3700, "error.review.not_found", HttpStatus.NOT_FOUND),
    REVIEW_RATING_INVALID_MIN(3701, "error.review.rating_invalid_min", HttpStatus.BAD_REQUEST),
    REVIEW_RATING_INVALID_MAX(3702, "error.review.rating_invalid_max", HttpStatus.BAD_REQUEST),
    REVIEW_TYPE_REQUIRED(3703, "error.review.type_required", HttpStatus.BAD_REQUEST),
    REVIEW_COACH_ID_REQUIRED(3704, "error.review.coach_id_required", HttpStatus.BAD_REQUEST),

    // Timetable Errors (3800-3899)
    TIMETABLE_NOT_FOUND(3800, "error.timetable.not_found", HttpStatus.NOT_FOUND),
    TIMETABLE_NAME_REQUIRED(3801, "error.timetable.name_required", HttpStatus.BAD_REQUEST),
    TIMETABLE_DESCRIPTION_REQUIRED(3802, "error.timetable.description_required", HttpStatus.BAD_REQUEST),
    TIMETABLE_STARTED_AT_REQUIRED(3803, "error.timetable.started_at_required", HttpStatus.BAD_REQUEST),
    TIMETABLE_ENDED_AT_REQUIRED(3804, "error.timetable.ended_at_required", HttpStatus.BAD_REQUEST),
    TIMETABLE_STARTED_AT_INVALID(3805, "error.timetable.started_at_invalid", HttpStatus.BAD_REQUEST),
    TIMETABLE_ENDED_AT_INVALID(3806, "error.timetable.ended_at_invalid", HttpStatus.BAD_REQUEST),

    // Score Errors (3900-3999)
    SCORE_NOT_FOUND(3806, "error.score.not_found", HttpStatus.NOT_FOUND),

    // Contact (4000-4099)
    NAME_REQUIRED(4001, "error.contact.name_required", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VALID(4003, "error.contact.email_not_valid", HttpStatus.BAD_REQUEST),
    MAIL_REQUIRED(4002, "error.contact.email_required", HttpStatus.BAD_REQUEST),
    MAIL_SUBJECT_REQUIRED(4004, "error.contact.subject_required", HttpStatus.BAD_REQUEST),
    CONTENT_RESTRICTED(4005, "error.contact.content_required", HttpStatus.BAD_REQUEST),
    CONTENT_TOO_SHORT(4006, "error.contact.content_too_short", HttpStatus.BAD_REQUEST),
    CONTENT_TOO_LONG(4007, "error.contact.content_too_long", HttpStatus.BAD_REQUEST),

    ;
    int code;
    String messageLocaleKey;
    HttpStatusCode httpCode;
}
