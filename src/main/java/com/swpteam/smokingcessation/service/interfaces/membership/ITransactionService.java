package com.swpteam.smokingcessation.service.interfaces.membership;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Transaction;

public interface ITransactionService {

    public Transaction createTransaction(Account account, double amount);

    public void makeAsPaid(String transactionId);
}
