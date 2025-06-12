package com.swpteam.smokingcessation.apis.transaction.services;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.transaction.Transaction;
import com.swpteam.smokingcessation.apis.transaction.TransactionRepository;
import com.swpteam.smokingcessation.apis.transaction.enums.TransactionStatus;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
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
public class TransactionService {

    TransactionRepository transactionRepository;
    AccountRepository accountRepository;

    @Transactional
    public Transaction createTransaction(Account account, double amount) {
        accountRepository.findById(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Transaction transaction = Transaction.startTransaction(account);

        transaction.setAmount(amount);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void makeAsPaid(String transactionId) {
        Transaction transaction = transactionRepository.findByIdAndIsDeletedFalse(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        transaction.setStatus(TransactionStatus.COMPLETED);

        transactionRepository.save(transaction);
    }
}
