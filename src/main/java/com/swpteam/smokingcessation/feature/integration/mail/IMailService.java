package com.swpteam.smokingcessation.feature.integration.mail;

import com.swpteam.smokingcessation.domain.dto.contact.ContactRequest;
import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.dto.plan.PlanResponse;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Message;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;

import java.time.LocalDate;

public interface IMailService {

    void sendVerificationEmail(String to, String username, String verificationLink);

    void sendPaymentSuccessEmail(String to, String subscriptionId, double amount);

    void sendMotivationMail(String to, Message message);

    void sendReminderMail(String to);

    void sendResetPasswordEmail(String to, String resetLink, String userName);

    void sendReportEmail(String to, ReportSummaryResponse report);

    void sendPhaseSummary(
            String planName,
            LocalDate startDate,
            LocalDate endDate,
            long totalReportedDays,
            long totalNotReportedDays,
            int totalMostSmoked,
            double successRate,
            PhaseStatus phaseStatus,
            String accountId
    );
    void sendPlanSummary(    String planName,
                             LocalDate startDate,
                             LocalDate endDate,
                             long totalReportedDays,
                             long totalNotReportedDays,
                             int totalMostSmoked,
                             Integer totalLeastSmoked,
                             String accountId,
                             PlanStatus planStatus,
                             Double successRate
                             ) ;

    void sendBookingRequestEmail(String to, BookingRequest request, String username, String coachName, String bookingLink);

    void sendContactMail(ContactRequest request);
}
