package com.swpteam.smokingcessation.service.impl.report;

import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryRequest;
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
    public ReportSummaryResponse getSummary(ReportSummaryRequest request) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = request.getFrom();
        if (request.getTo() != null) {
            to = request.getTo();
        }

        double revenue = transactionRepository.sumAmountBetween(from, to);
        int newAccounts = accountRepository.countByCreatedAtBetween(from, to);
        int currentAccounts = (int) accountRepository.count();
        int activeAccounts = accountRepository.countActiveUsersBetween(from, to);

        return ReportSummaryResponse.builder()
                .revenue(revenue)
                .newAccounts(newAccounts)
                .currentAccounts(currentAccounts)
                .activeAccounts(activeAccounts)
                .fromDate(from)
                .toDate(to)
                .build();
    }
}
