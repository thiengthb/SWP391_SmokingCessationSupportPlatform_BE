package com.swpteam.smokingcessation.apis.mail;

import com.swpteam.smokingcessation.apis.message.entity.Message;
import com.swpteam.smokingcessation.apis.message.enums.MessageType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class MailService {

    @Autowired
    //inject cấu hinh
    private JavaMailSender mailSender;
    //interface mặc định của spring để gửi mail
    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendMotivationMail(String to,Message message) throws  MessagingException, IOException{
        // Create MimeMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        String content=message.getContent();
        helper.setSubject("💪 Daily Motivation");
        helper.setTo(to);
        Context context=new Context();
        context.setVariable("quote", content);
        context.setVariable("sendTime", LocalDateTime.now());
        String htmlContent= templateEngine.process("motivation-template", context);
        helper.setText(htmlContent, true); // true chỉ định nội dung là HTML
        // Gửi email
        mailSender.send(mimeMessage);
    }

    public void sendReminderMail(String to,Message message) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        String content=message.getContent();
        helper.setSubject("⏰ Friendly Reminder");
        helper.setTo(to);
        Context context=new Context();
        context.setVariable("reminder", content);
        context.setVariable("deadline",LocalDateTime.now().plusMinutes(30));
        context.setVariable("sendTime", LocalDateTime.now());
        String htmlContent= templateEngine.process("reminder-template", context);
        helper.setText(htmlContent, true); // true chỉ định nội dung là HTML
        // Gửi email
        mailSender.send(mimeMessage);



    }




    }






