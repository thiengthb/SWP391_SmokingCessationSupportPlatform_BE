package com.swpteam.smokingcessation.feature.version1.membership.service;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Transaction;

public interface ITransactionService {

    Transaction createTransaction(Account account, double amount);

    void makeAsPaid(String transactionId);
}
