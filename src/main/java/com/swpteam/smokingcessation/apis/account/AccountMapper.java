package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.dto.AccountCreateRequest;
import com.swpteam.smokingcessation.apis.account.dto.AccountResponse;
import com.swpteam.smokingcessation.apis.account.dto.AccountUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Account toAccount(AccountCreateRequest request);

    AccountResponse toAccountResponse(Account entity);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateAccount(@MappingTarget Account entity, AccountUpdateRequest request);
}