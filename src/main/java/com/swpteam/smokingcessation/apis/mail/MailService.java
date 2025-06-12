package com.swpteam.smokingcessation.apis.mail;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.message.entity.Message;
import com.swpteam.smokingcessation.apis.message.enums.MessageType;
import com.swpteam.smokingcessation.apis.subscription.Subscription;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {

    JavaMailSender mailSender;

    TemplateEngine templateEngine;

    @NonFinal
    @Value("${spring.mail.username}")
    String hostEmail;

    public void sendPaymentSuccessEmail(Account account, Subscription subscription, double amount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariable("userName", account.getUsername());
            context.setVariable("amount", String.format("%.2f", amount / 100.0));
            context.setVariable("startDate", subscription.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            context.setVariable("endDate", subscription.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            String html = templateEngine.process("payment-success", context);

            helper.setTo(account.getEmail());
            helper.setSubject("Payment Successful - Subscription ID " + subscription.getId());
            helper.setText(html, true);
            helper.setFrom(hostEmail);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendSimpleMail(String to, Message message) throws MessagingException, IOException {
        //to người nhận, subject tiêu đề, content: nội dung
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String subject = getSubjectByType(message.getType());
        String htmlBody = buildTemplateHtml(subject, message.getContent());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true: cho phép HTML

        mailSender.send(mimeMessage);
    }

    private String getSubjectByType(MessageType type) {
        return switch (type) {
            case REMINDER -> "⏰ Friendly Reminder";
            case MOTIVATION -> "💪 Daily Motivation";
            case ADVICE -> "🧠 Health Advice";
        };
    }
    private String buildTemplateHtml(String title, String content) throws IOException {
        String template = new String(
                Files.readAllBytes(Paths.get("src/main/resources/mail-template.html")),
                StandardCharsets.UTF_8
        );
        return String.format(template, title, content);
    }
}



