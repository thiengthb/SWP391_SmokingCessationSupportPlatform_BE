package com.swpteam.smokingcessation.integration.mail;

import com.swpteam.smokingcessation.domain.dto.report.PlanSummaryResponse;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Message;

public interface IMailService {

    void sendPaymentSuccessEmail(String to, String subscriptionId, double amount);

    void sendMotivationMail(String to, Message message);

    void sendReminderMail(String to);

    void sendResetPasswordEmail(String to, String resetLink, String userName);

    void sendReportEmail(String to, ReportSummaryResponse report);

    void sendPlanSummaryEmail(Account account, PlanSummaryResponse summary);
}
