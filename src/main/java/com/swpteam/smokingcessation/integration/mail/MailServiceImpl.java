package com.swpteam.smokingcessation.integration.mail;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.dto.report.PlanSummaryResponse;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Message;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.membership.ISubscriptionService;
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
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public void sendVerificationEmail(String to, String username, String verificationLink) {
        buildAndSendMail(
                "Verify Email",
                to,
                "verification-email",
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
                account.getEmail(),
                "motivation-template",
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
    public void sendMotivationMail(String to, Message message) {
        buildAndSendMail(
                "💪 Daily Motivation",
                to,
                "motivation-template",
                List.of(
                        Map.entry("quote", message.getContent()),
                        Map.entry("sendTime", LocalDateTime.now())
                )
        );
        log.info("Motivation mail sent to {}", to);
    }

    @Override
    public void sendReminderMail(String to) {
        buildAndSendMail(
                "⏰ Friendly Reminder",
                to,
                "reminder-template",
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
                to,
                "reset-mail-template",
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
                to,
                "monthly-report-email",
                List.of(
                        Map.entry("report", report)
                )
        );
        log.info("Report mail sent to {}", to);
    }

    @Override
    public void sendPhaseSummary(String to, PhaseResponse phaseResponse) {
        buildAndSendMail(
                "Phase Summary Report",
                to,
                "phase-summary-template", // Tên file template thymeleaf bạn sẽ tạo sau
                List.of(
                        Map.entry("phase", phaseResponse.getPhase()),
                        Map.entry("phaseName", phaseResponse.getPhaseName()),
                        Map.entry("startDate", phaseResponse.getStartDate()),
                        Map.entry("endDate", phaseResponse.getEndDate()),
                        Map.entry("successRate", String.format("%.2f%%", phaseResponse.getSuccessRate())),
                        Map.entry("phaseResult", phaseResponse.getPhaseStatus())
                )
        );
        log.info("Phase summary mail sent to {}", to);
    }

    @Override
    public void sendPlanSummary(String to, PlanResponse planResponse) {
        buildAndSendMail(
                "Plan Summary Report",
                to,
                "plan-summary-template",
                List.of(

                )
        );
    }

    public void sendBookingRequestEmail(String to, BookingRequest request, String username, String coachName, String bookingLink) {
        LocalDateTime startedAt = DateTimeUtil.reformat(request.startedAt());
        LocalDateTime endedAt = DateTimeUtil.reformat(request.endedAt());
        buildAndSendMail("New Booking Request",
                to,
                "coach-booking-request",
                List.of(
                        Map.entry("startedAt", startedAt),
                        Map.entry("endedAt", endedAt),
                        Map.entry("memberName", username),
                        Map.entry("bookingLink", bookingLink),
                        Map.entry("coachName", coachName)
                ));
    }

    private void buildAndSendMail(
            String title,
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
            helper.setFrom(hostEmail);

            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            log.error(exception.getMessage());
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

}



