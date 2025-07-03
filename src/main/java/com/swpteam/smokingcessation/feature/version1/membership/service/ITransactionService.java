package com.swpteam.smokingcessation.feature.version1.membership.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.transaction.TransactionListItemResponse;
import com.swpteam.smokingcessation.domain.dto.transaction.TransactionResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Transaction;

public interface ITransactionService {

    PageResponse<TransactionListItemResponse> getMyTransactionsPage(PageableRequest request);

    TransactionResponse getTransactionById(String id);

    Transaction createTransaction(Account account, double amount);

    void makeAsPaid(String transactionId);
}
