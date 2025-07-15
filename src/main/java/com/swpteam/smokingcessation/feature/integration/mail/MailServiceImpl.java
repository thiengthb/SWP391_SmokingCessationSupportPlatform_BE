package com.swpteam.smokingcessation.feature.integration.mail;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.contact.ContactRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Message;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.feature.version1.membership.service.ISubscriptionService;
import com.swpteam.smokingcessation.utils.DateTimeUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailServiceImpl implements IMailService {

    JavaMailSender mailSender;
    SpringTemplateEngine templateEngine;

    IAccountService accountService;
    ISubscriptionService subscriptionService;

    @NonFinal
    @Value("${spring.mail.username}")
    String hostEmail;

    private static final String VERIFICATION_EMAIL = "email/verification_email";
    private static final String PAYMENT_SUCCESS_EMAIL = "email/payment_success_email";
    private static final String MOTIVATION_EMAIL = "email/motivation_email";
    private static final String REMINDER_EMAIL = "email/reminder_email";
    private static final String RESET_PASSWORD_EMAIL = "email/reset_password_email";
    private static final String MONTHLY_REPORT_EMAIL = "email/monthly_report_email";
    private static final String BOOKING_REQUEST_EMAIL = "email/booking_request_email";
    private static final String PLAN_SUMMARY_EMAIL = "email/plan_summary_email";
    private static final String PHASE_SUMMARY_EMAIL = "email/phase_summary_email";
    private static final String BOOKING_CANCELLED_COACH = "email/booking_cancelled_by_member";
    private static final String BOOKING_CANCELLED_SEND_MEMBER = "email/booking_reject";
    private static final String BOOKING_APPROVED = "email/booking_approved";



    @Override
    public void sendVerificationEmail(String to, String username, String verificationLink) {
        buildAndSendMail(
                "Verify Email",
                hostEmail,
                to,
                VERIFICATION_EMAIL,
                List.of(
                        Map.entry("username", username),
                        Map.entry("verificationLink", verificationLink)
                )
        );
        log.info("Verification email sent to {}", to);
    }

    @Override
    public void sendPaymentSuccessEmail(String to, String subscriptionId, double amount) {

        Account account = accountService.findAccountByEmailOrThrowError(to);
        Subscription subscription = subscriptionService.findSubscriptionByIdOrThrowError(subscriptionId);

        LocalDate startDate = DateTimeUtil.reformat(subscription.getStartDate());
        LocalDate endDate = DateTimeUtil.reformat(subscription.getEndDate());

        buildAndSendMail(
                "Payment Successful for Subscription ID " + subscription.getId(),
                hostEmail,
                account.getEmail(),
                PAYMENT_SUCCESS_EMAIL,
                List.of(
                        Map.entry("username", account.getUsername()),
                        Map.entry("amount", String.format("%.2f", amount / 100.0)),
                        Map.entry("startDate", startDate),
                        Map.entry("endDate", endDate)
                )
        );
        log.info("Payment success mail sent to {}", to);
    }

    @Override
    public void sendMotivationMail(String to, String motivation) {
        buildAndSendMail(
                "💪 Daily Motivation",
                hostEmail,
                to,
                MOTIVATION_EMAIL,
                List.of(
                        Map.entry("quote", motivation),
                        Map.entry("sendTime", LocalDateTime.now())
                )
        );
        log.info("Motivation mail sent to {}", to);
    }

    @Override
    public void sendReminderMail(String to) {
        buildAndSendMail(
                "⏰ Friendly Reminder",
                hostEmail,
                to,
                REMINDER_EMAIL,
                List.of(
                        Map.entry("deadline", LocalDateTime.now().plusMinutes(30)),
                        Map.entry("resetLink", LocalDateTime.now())
                )
        );
        log.info("Reminder mail sent to {}", to);
    }

    @Override
    public void sendResetPasswordEmail(String to, String resetLink, String username) {
        buildAndSendMail(
                "Reset password",
                hostEmail,
                to,
                RESET_PASSWORD_EMAIL,
                List.of(
                        Map.entry("userName", username),
                        Map.entry("resetLink", resetLink),
                        Map.entry("expirationTime", "15 minutes")
                )
        );
        log.info("Reset password mail sent to {}", to);
    }

    @Override
    public void sendReportEmail(String to, ReportSummaryResponse report) {
        buildAndSendMail(
                "Monthly performance report",
                hostEmail,
                to,
                MONTHLY_REPORT_EMAIL,
                List.of(
                        Map.entry("report", report)
                )
        );
        log.info("Report mail sent to {}", to);
    }

    @Override
    public void sendPhaseSummary(String planName, LocalDate startDate, LocalDate endDate, long totalReportedDays, long totalNotReportedDays, int totalMostSmoked, double successRate, PhaseStatus phaseStatus, String mail,String healthImprovedSummary) {
        buildAndSendMail(
                "Phase Summary Report",
                hostEmail,
                mail,
                PHASE_SUMMARY_EMAIL,
                List.of(
                        Map.entry("planName", planName),
                        Map.entry("startDate", startDate),
                        Map.entry("endDate", endDate),
                        Map.entry("totalReportedDays", totalReportedDays),
                        Map.entry("totalNotReportedDays", totalNotReportedDays),
                        Map.entry("totalMostSmoked", totalMostSmoked),
                        Map.entry("successRate", String.format("%.2f", successRate)),
                        Map.entry("phaseStatus", phaseStatus.toString()),
                        Map.entry("healthImproved",healthImprovedSummary)
                )
        );
        log.info("Phase summary mail sent to {}", mail);
    }

    @Override
    public void sendPlanSummary(
            String planName,
            LocalDate startDate,
            LocalDate endDate,
            long totalReportedDays,
            long totalNotReportedDays,
            int totalMostSmoked,
            Integer totalLeastSmoked,
            String email,
            PlanStatus planStatus,
            Double successRate
    ) {
        buildAndSendMail(
                "Plan Summary Report",
                hostEmail,
                email,
                PLAN_SUMMARY_EMAIL,
                List.of(
                        Map.entry("planName", planName),
                        Map.entry("startDate", startDate),
                        Map.entry("endDate", endDate),
                        Map.entry("totalReportedDays", totalReportedDays),
                        Map.entry("totalNotReportedDays", totalNotReportedDays),
                        Map.entry("totalMostSmoked", totalMostSmoked),
                        Map.entry("totalLeastSmoked", totalLeastSmoked),
                        Map.entry("planStatus", planStatus),
                        Map.entry("successRate", successRate)
                )
        );
        log.info("Plan summary mail sent to {}", email);
    }

    @Override
    public void sendBookingRequestEmail(String to, BookingRequest request, String username, String coachName, String bookingLink) {
        LocalDateTime startedAt = DateTimeUtil.reformat(request.startedAt());
        LocalDateTime endedAt = DateTimeUtil.reformat(request.endedAt());
        buildAndSendMail("New Booking Request",
                hostEmail,
                to,
                BOOKING_REQUEST_EMAIL,
                List.of(
                        Map.entry("startedAt", startedAt),
                        Map.entry("endedAt", endedAt),
                        Map.entry("memberName", username),
                        Map.entry("bookingLink", bookingLink),
                        Map.entry("coachName", coachName)
                ));
    }


    @Override
    public void sendUpcomingBookingReminderMail(String to, String coachId, LocalDateTime startTime, String coachName) {
        LocalDateTime startedAt = DateTimeUtil.reformat(startTime);
        buildAndSendMail("Upcoming booking"
                , hostEmail,
                to,
                BOOKING_REQUEST_EMAIL,
                List.of(
                        Map.entry("startedAt", startedAt),
                        Map.entry("coachName", coachName)
                )

        );
    }

    @Override
    public void sendBookingCancelledEmail(String to, String memberName, LocalDateTime startedAt, LocalDateTime endedAt) {
        buildAndSendMail(
                "Booking Cancelled",
                hostEmail,
                to,
                BOOKING_CANCELLED_COACH,
                List.of(
                        Map.entry("memberName", memberName),
                        Map.entry("startedAt", DateTimeUtil.reformat(startedAt)),
                        Map.entry("endedAt", DateTimeUtil.reformat(endedAt))
                )
        );
    }

    @Override
    public void sendContactMail(ContactRequest request) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(hostEmail);
            helper.setFrom(hostEmail);
            helper.setReplyTo(request.email());
            helper.setSubject("New Message From User: " + request.subject());

            String textContent = "Name: " + request.name() + "\n" +
                    "Email: " + request.email() + "\n" +
                    "Subject: " + request.subject() + "\n\n" +
                    "Content:\n" +
                    request.content();

            helper.setText(textContent, false);

            mailSender.send(mimeMessage);

            log.info("Contact mail sent from {} to system email", request.email());
        } catch (MessagingException e) {
            log.error("Failed to send contact mail: {}", e.getMessage());
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    @Async
    @Override
    public void sendRejectNotificationMail(String to, String content) {
        log.info("nội dung đc gửi đi: {}",content);
        buildAndSendMail(
                "Booking Rejected",
                hostEmail,
                to,
                BOOKING_CANCELLED_SEND_MEMBER ,
                List.of(
                        Map.entry("content", content)
                )
        );
    }

    @Async
    @Override
    public void sendApprovedNotificationMail(String to, String content) {
        buildAndSendMail(
                "Booking Approved",
                hostEmail,
                to,
                BOOKING_APPROVED,
                List.of(
                        Map.entry("content", content)
                )
        );
    }


    private void buildAndSendMail(
            String title,
            String from,
            String to,
            String templateName,
            List<Map.Entry<String, Object>> contextVariables
    ) {
        Context context = new Context();
        for (Map.Entry<String, Object> entry : contextVariables) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        String htmlContent = templateEngine.process(templateName, context);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(htmlContent, true);
            helper.setFrom(from);

            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            log.error(exception.getMessage());
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

}



