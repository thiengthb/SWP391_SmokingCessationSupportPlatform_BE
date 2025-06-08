package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.dto.request.AccountCreateRequest;
import com.swpteam.smokingcessation.apis.account.dto.request.AccountUpdateRequest;
import com.swpteam.smokingcessation.apis.account.dto.response.AccountResponse;
import com.swpteam.smokingcessation.apis.account.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Account toAccount(AccountCreateRequest request);

    AccountResponse toAccountResponse(Account entity);

    @Mapping(target = "id", ignore = true)
    //@Mapping(target = "email", ignore = true)
    //@Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateAccount(@MappingTarget Account entity, AccountUpdateRequest request);
}