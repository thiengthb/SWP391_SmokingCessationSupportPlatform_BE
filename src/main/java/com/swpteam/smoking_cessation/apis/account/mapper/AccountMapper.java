package com.swpteam.smoking_cessation.apis.account.mapper;

import com.swpteam.smoking_cessation.apis.account.dto.request.AccountRequest;
import com.swpteam.smoking_cessation.apis.account.dto.response.AccountResponse;
import com.swpteam.smoking_cessation.apis.account.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Account toEntity(AccountRequest request);

    @Mapping(target = "accountId", source = "id")
    AccountResponse toResponse(Account entity);

    List<AccountResponse> toResponseList(List<Account> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(@MappingTarget Account entity, AccountRequest request);
}