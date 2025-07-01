package com.swpteam.smokingcessation.integration.mail;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.contact.ContactRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Message;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
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
                hostEmail,
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
                hostEmail,
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
                hostEmail,
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
                hostEmail,
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
                hostEmail,
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
                hostEmail,
                to,
                "monthly-report-email",
                List.of(
                        Map.entry("report", report)
                )
        );
        log.info("Report mail sent to {}", to);
    }

    @Override
    public void sendPhaseSummary(String planName, LocalDate startDate, LocalDate endDate, long totalReportedDays, long totalNotReportedDays, int totalMostSmoked, double successRate, PhaseStatus phaseStatus, String accountId) {
        buildAndSendMail(
                "Phase Summary Report",
                hostEmail,
                accountId,
                "phase-summary-template", // Tên file template Thymeleaf cho phase
                List.of(
                        Map.entry("planName", planName),
                        Map.entry("startDate", startDate),
                        Map.entry("endDate", endDate),
                        Map.entry("totalReportedDays", totalReportedDays),
                        Map.entry("totalNotReportedDays", totalNotReportedDays),
                        Map.entry("totalMostSmoked", totalMostSmoked),
                        Map.entry("successRate", String.format("%.2f", successRate)),
                        Map.entry("phaseStatus", phaseStatus.toString())
                )
        );

        log.info("Phase summary mail sent to {}", accountId);
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
            String accountId,
            PlanStatus planStatus,
            Double successRate
    ) {
        buildAndSendMail(
                "Plan Summary Report",
                hostEmail,
                accountId,
                "plan-summary-template", // Tên file template thymeleaf bạn tạo sau
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

        log.info("Plan summary mail sent to {}", accountId);
    }

    @Override
    public void sendBookingRequestEmail(String to, BookingRequest request, String username, String coachName, String bookingLink) {

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

            StringBuilder textContent = new StringBuilder();
            textContent
                    .append("Name: ").append(request.name()).append("\n")
                    .append("Email: ").append(request.email()).append("\n")
                    .append("Subject: ").append(request.subject()).append("\n\n")
                    .append("Content:\n")
                    .append(request.content());

            helper.setText(textContent.toString(), false);

            mailSender.send(mimeMessage);

            log.info("Contact mail sent from {} to system email", request.email());
        } catch (MessagingException e) {
            log.error("Failed to send contact mail: {}", e.getMessage());
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
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



