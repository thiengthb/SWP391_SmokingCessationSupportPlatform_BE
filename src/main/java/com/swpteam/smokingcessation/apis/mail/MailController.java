package com.swpteam.smokingcessation.apis.mail;

import com.swpteam.smokingcessation.apis.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/send-template")
    public String sendMailTemplate(@RequestParam String to, @RequestBody Message message) {
    return null;
    }
}
