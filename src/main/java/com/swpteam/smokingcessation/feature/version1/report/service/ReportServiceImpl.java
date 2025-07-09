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

    IReportRepository IReportRepository;
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

        return IReportRepository.getReportSummary(from, to);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserActivityResponse> getUserGrowth(ReportSummaryRequest request) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = request.from();
        if (request.to() != null) {
            to = request.to();
        }


        return IReportRepository.getUserActivity(from, to);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDistributionResponse getUserDistribution() {
        return IReportRepository.getUserDistribution();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<RevenueResponse> getRevenue(ReportSummaryRequest request){
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = request.from();
        if (request.to() != null) {
            to = request.to();
        }
        return IReportRepository.getRevenue(from, to);
    }
}
