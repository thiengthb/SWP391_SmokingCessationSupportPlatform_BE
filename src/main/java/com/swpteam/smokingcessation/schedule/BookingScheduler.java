package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.Booking;
import com.swpteam.smokingcessation.domain.enums.BookingStatus;
import com.swpteam.smokingcessation.feature.integration.mail.IMailService;
import com.swpteam.smokingcessation.feature.version1.booking.service.IBookingService;
import com.swpteam.smokingcessation.feature.version1.notification.service.INotificationService;
import com.swpteam.smokingcessation.repository.jpa.BookingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingScheduler {

    BookingRepository bookingRepository;
    INotificationService notificationService;
    IMailService mailService;

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void notifyUpcomingBookings() {
        log.info("Notify bookings in next 2 hours");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursLater = now.plusHours(2);

        List<Booking> bookings = bookingRepository.findAllByStatusAndStartedAtBetweenAndIsDeletedFalseAndNotifyBeforeBookingFalse(
                BookingStatus.APPROVED,
                now,
                twoHoursLater
        );

        log.info("Found {} bookings to notify", bookings.size());

        for (Booking booking : bookings) {
            try {
                notificationService.sendUpcomingBookingNotification(
                        booking.getMember().getId(),
                        booking.getStartedAt()
                );

                mailService.sendUpcomingBookingReminderMail(
                        booking.getMember().getEmail(),
                        booking.getCoach().getId(),
                        booking.getStartedAt(),
                        booking.getCoach().getUsername()
                );

                booking.setNotifyBeforeBooking(true);
                bookingRepository.save(booking);

                log.info("Notified booking id={}", booking.getId());
            } catch (Exception e) {
                log.error("Error notifying booking id={}", booking.getId(), e);
            }
        }

        log.info("BookingScheduler: Done");
    }

    /*@Scheduled(cron = "0 59 23 * * *")
    public void autoRejectPendingBookings() {
        log.info("reset pending bookings");

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Booking> pendingBookings = bookingRepository.findAllByStatusAndStartedAtBetweenAndIsDeletedFalse(
                BookingStatus.PENDING,
                startOfDay,
                endOfDay
        );

        log.info("Found {} pending bookings to reject", pendingBookings.size());

        for (Booking booking : pendingBookings) {
            booking.setStatus(BookingStatus.REJECTED);
            booking.setDeclineReason("Auto expired");

            try {
                notificationService.sendBookingRejectNotification(
                        "Your booking expired and was auto-rejected.",
                        booking.getMember().getId()
                );

                mailService.sendRejectNotificationMail(
                        booking.getMember().getEmail(),
                        "Your booking expired and was auto-rejected."
                );
            } catch (Exception e) {
                log.error("Error sending notification for booking id={}", booking.getId(), e);
            }
        }

        bookingRepository.saveAll(pendingBookings);
        log.info("BookingScheduler: Completed resetting pending bookings");
    }


     */
}

