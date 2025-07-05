package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.account.AccountRequest;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.dto.account.AccountUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.auth.request.RegisterRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toEntity(AccountRequest request);

    @Mapping(target = "havingSubscription", expression = "java(entity.isHavingSubscription())")
    AccountResponse toResponse(Account entity);

    void update(@MappingTarget Account entity, AccountUpdateRequest request);

    void updateWithoutRole(@MappingTarget Account entity, AccountUpdateRequest request);
}