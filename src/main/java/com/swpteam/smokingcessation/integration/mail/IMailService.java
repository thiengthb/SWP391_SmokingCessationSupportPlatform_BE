package com.swpteam.smokingcessation.integration.mail;

import com.swpteam.smokingcessation.domain.entity.Message;
import jakarta.mail.MessagingException;

public interface IMailService {

    public void sendPaymentSuccessEmail(String accountId, String subscriptionId, double amount);

    public void sendMotivationMail(String to, Message message);

    public void sendReminderMail(String to);

    public void sendResetPasswordEmail(String to, String resetLink, String userName) throws MessagingException;
}
