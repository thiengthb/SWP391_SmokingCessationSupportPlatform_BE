package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.transaction.TransactionListItemResponse;
import com.swpteam.smokingcessation.domain.dto.transaction.TransactionResponse;
import com.swpteam.smokingcessation.domain.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionListItemResponse toListItemResponse(Transaction entity);

    @Mapping(source = "account.id", target = "accountId")
    TransactionResponse toResponse(Transaction entity);
}
