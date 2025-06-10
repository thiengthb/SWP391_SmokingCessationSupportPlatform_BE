package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.dto.AccountCreateRequest;
import com.swpteam.smokingcessation.apis.account.dto.AccountResponse;
import com.swpteam.smokingcessation.apis.account.dto.AccountUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toEntity(AccountCreateRequest request);

    AccountResponse toResponse(Account entity);

    void updateAccount(@MappingTarget Account entity, AccountUpdateRequest request);
}