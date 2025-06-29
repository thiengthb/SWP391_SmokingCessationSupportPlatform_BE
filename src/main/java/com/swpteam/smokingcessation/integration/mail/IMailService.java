package com.swpteam.smokingcessation.integration.mail;

import com.swpteam.smokingcessation.domain.dto.contact.ContactRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.dto.report.PlanSummaryResponse;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Message;

public interface IMailService {

    void sendVerificationEmail(String to, String username, String verificationLink);

    void sendPaymentSuccessEmail(String to, String subscriptionId, double amount);

    void sendMotivationMail(String to, Message message);

    void sendReminderMail(String to);

    void sendResetPasswordEmail(String to, String resetLink, String userName);

    void sendReportEmail(String to, ReportSummaryResponse report);

    void sendPhaseSummary(String to, PhaseResponse phaseResponse);

    void sendPlanSummary(String to, PlanResponse planResponse);

    void sendBookingRequestEmail(String to, BookingRequest request, String username, String coachName, String bookingLink);

    void sendContactMail(ContactRequest request);
}
