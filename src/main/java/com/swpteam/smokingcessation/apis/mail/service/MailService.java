package com.swpteam.smokingcessation.apis.mail.service;

import com.swpteam.smokingcessation.apis.message.entity.Message;
import com.swpteam.smokingcessation.apis.message.enums.MessageType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class MailService {

    @Autowired
    //inject cấu hinh
    private JavaMailSender mailSender;
    //interface mặc định của spring để gửi mail

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

    private void sendHtml(String to, String subject, String htmlContent) throws MessagingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true for HTML
        mailSender.send(message);
    }

    public void sendTemplatedHtml(String to, String subject, String title, String content) throws MessagingException, IOException {
        String html = buildTemplateHtml(title, content);
        sendHtml(to, subject, html);
    }
}



