package com.swpteam.smokingcessation.feature.version1.membership.service.impl;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Transaction;
import com.swpteam.smokingcessation.repository.jpa.TransactionRepository;
import com.swpteam.smokingcessation.domain.enums.TransactionStatus;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.feature.version1.membership.service.ITransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionServiceImpl implements ITransactionService {

    TransactionRepository transactionRepository;
    IAccountService accountService;

    @Override
    @Transactional
    public Transaction createTransaction(Account account, double amount) {
        accountService.findAccountByIdOrThrowError(account.getId());

        Transaction transaction = Transaction.startTransaction(account);
        transaction.setAmount(amount);

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void makeAsPaid(String transactionId) {
        Transaction transaction = findTransactionById(transactionId);

        transaction.setStatus(TransactionStatus.COMPLETED);

        transactionRepository.save(transaction);
    }

    @Transactional
    private Transaction findTransactionById(String id) {
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
