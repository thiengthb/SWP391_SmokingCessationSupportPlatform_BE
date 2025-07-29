package com.swpteam.smokingcessation.feature.integration.mail;

import com.swpteam.smokingcessation.domain.dto.contact.ContactRequest;
import com.swpteam.smokingcessation.domain.dto.booking.BookingRequest;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.entity.Message;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IMailService {

    void sendVerificationEmail(String to, String username, String verificationLink);

    void sendPaymentSuccessEmail(String to, String subscriptionId, double amount);

    void sendMotivationMail(String to, String motivation);

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
            String mail,
            String healthImprovedSummary
    );
    void sendPlanSummary(    String planName,
                             LocalDate startDate,
                             LocalDate endDate,
                             long totalReportedDays,
                             long totalNotReportedDays,
                             int totalMostSmoked,
                             int totalLeastSmoked,
                             String email,
                             PlanStatus planStatus,
                             Double successRate
                             ) ;

    void sendBookingRequestEmail(String to, BookingRequest request, String username, String coachName, String bookingLink);

    void sendContactMail(ContactRequest request);

    void sendRejectNotificationMail(String to, String content);

    void sendApprovedNotificationMail(String to, String content);

    void sendUpcomingBookingReminderMail(String to, String coachId, LocalDateTime startTime,String coachName);

    void sendBookingCancelledEmail(String to, String memberName, LocalDateTime startedAt, LocalDateTime endedAt);


}
