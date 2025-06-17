package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.domain.entity.Report;
import com.swpteam.smokingcessation.integration.mail.IMailService;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.ReportRepository;
import com.swpteam.smokingcessation.repository.TransactionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MonthlyReportScheduler {
    AccountRepository accountRepository;
    ReportRepository reportRepository;
    TransactionRepository transactionRepository;
    IMailService mailService;


    @Scheduled(cron = "0 0 8 1 * *")
    public void generateAndSendMonthlyReport() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        // Calculate revenue
        double revenue = transactionRepository.sumAmountBetween(startOfMonth, endOfMonth);

        // Count new users this month
        int newAccounts = accountRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);

        // Count active users (e.g., users who logged in or performed an action this month)
        int activeAccounts = accountRepository.countActiveUsersBetween(startOfMonth, endOfMonth);

        // Total accounts
        int currentAccounts = (int) accountRepository.count();

        Report report = Report.builder()
                .revenue(revenue)
                .newAccounts(newAccounts)
                .activeAccounts(activeAccounts)
                .currentAccounts(currentAccounts)
                .build();

        reportRepository.save(report);

        List<String> adminEmails = accountRepository.findAllAdminEmails();
        for (String email : adminEmails) {
            mailService.sendReportEmail(email, report);
        }
        log.info("Monthly report generated and sent to admins.");
    }
}
