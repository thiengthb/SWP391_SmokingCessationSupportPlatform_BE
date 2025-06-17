package com.swpteam.smokingcessation.service.impl.report;

import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.TransactionRepository;
import com.swpteam.smokingcessation.service.interfaces.report.IReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {

    TransactionRepository transactionRepository;
    AccountRepository accountRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ReportSummaryResponse getSummary(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(days).withHour(0).withMinute(0).withSecond(0).withNano(0);

        double revenue = transactionRepository.sumAmountBetween(from, now);
        int newAccounts = accountRepository.countByCreatedAtBetween(from, now);
        int currentAccounts = (int) accountRepository.count();
        int activeAccounts = accountRepository.countActiveUsersBetween(from, now);

        return ReportSummaryResponse.builder()
                .revenue(revenue)
                .newAccounts(newAccounts)
                .currentAccounts(currentAccounts)
                .activeAccounts(activeAccounts)
                .fromDate(from)
                .toDate(now)
                .build();
    }
}
