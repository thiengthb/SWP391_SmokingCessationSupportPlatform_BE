package com.swpteam.smokingcessation.feature.version1.membership.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.transaction.TransactionListItemResponse;
import com.swpteam.smokingcessation.domain.dto.transaction.TransactionResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.domain.entity.Transaction;
import com.swpteam.smokingcessation.domain.enums.Currency;
import com.swpteam.smokingcessation.domain.enums.PaymentMethod;
import com.swpteam.smokingcessation.domain.mapper.TransactionMapper;
import com.swpteam.smokingcessation.repository.jpa.TransactionRepository;
import com.swpteam.smokingcessation.domain.enums.TransactionStatus;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.feature.version1.membership.service.ITransactionService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionServiceImpl implements ITransactionService {

    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;
    IAccountService accountService;
    AuthUtilService authUtilService;

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    public PageResponse<TransactionListItemResponse> getMyTransactionsPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Subscription.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Transaction> transactions = transactionRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(transactions.map(transactionMapper::toListItemResponse));
    }

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    public TransactionResponse getTransactionById(String id) {
        return transactionMapper.toResponse(findTransactionByIdOrThrowError(id));
    }

    @Override
    @Transactional
    public Transaction createTransaction(Account account, double amount, Currency currency) {
        accountService.findAccountByIdOrThrowError(account.getId());

        Transaction transaction = Transaction.startTransaction(account);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setMethod(PaymentMethod.CARD);

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void makeAsPaid(String transactionId) {
        Transaction transaction = findTransactionByIdOrThrowError(transactionId);

        transaction.setStatus(TransactionStatus.COMPLETED);

        transactionRepository.save(transaction);
    }

    @Transactional
    private Transaction findTransactionByIdOrThrowError(String id) {
        Transaction transaction = transactionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (transaction.getAccount().isDeleted()) {
            transaction.setDeleted(true);
            transactionRepository.save(transaction);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return transaction;
    }
}
