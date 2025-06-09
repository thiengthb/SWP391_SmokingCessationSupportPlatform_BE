package com.swpteam.smokingcessation.apis.mail;

import com.swpteam.smokingcessation.apis.message.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/send-template")
    public String sendMailTemplate(@RequestParam String to, @RequestBody Message message) {
        try {
            mailService.sendSimpleMail(to, message);
            return "Template mail sent successfully!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
