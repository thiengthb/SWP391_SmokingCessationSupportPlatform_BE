package com.swpteam.smokingcessation.feature.version1.report.service;

import com.swpteam.smokingcessation.domain.dto.report.*;
import com.swpteam.smokingcessation.repository.jpa.AccountRepository;
import com.swpteam.smokingcessation.repository.jpa.TransactionRepository;
import com.swpteam.smokingcessation.repository.report.IReportRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReportServiceImpl implements IReportService {

    IReportRepository reportRepository;
    AccountRepository accountRepository;
    TransactionRepository transactionRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ReportSummaryResponse getSummary(ReportSummaryRequest request) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = request.from();
        if (request.to() != null) {
            to = request.to();
        }

//        double revenue = transactionRepository.sumAmountBetween(from, to);
//        int newAccounts = accountRepository.countByCreatedAtBetween(from, to);
//        int currentAccounts = (int) accountRepository.count();
//        int activeAccounts = accountRepository.countActiveUsersBetween(from, to);

        return reportRepository.getReportSummary(from, to);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserActivityResponse> getUserGrowth(ReportSummaryRequest request) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = request.from();
        if (request.to() != null) {
            to = request.to();
        }


        return reportRepository.getUserActivity(from, to);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDistributionResponse getUserDistribution() {
        return reportRepository.getUserDistribution();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<RevenueResponse> getRevenue(ReportSummaryRequest request) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = request.from();
        if (request.to() != null) {
            to = request.to();
        }
        return reportRepository.getRevenue(from, to);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public PremiumDistributionResponse getPremiumDistribution() {
        return reportRepository.getPremiumDistribution();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<CompletionResponse> getCompletion(ReportSummaryRequest request) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = request.from();
        if (request.to() != null) {
            to = request.to();
        }
        return reportRepository.getCompletetion(from, to);
    }
}
