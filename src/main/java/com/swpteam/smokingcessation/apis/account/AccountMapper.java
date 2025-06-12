package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.dto.AccountRequest;
import com.swpteam.smokingcessation.apis.account.dto.AccountResponse;
import com.swpteam.smokingcessation.apis.authentication.dto.request.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toEntity(AccountRequest request);

    Account toEntityFromRegister(RegisterRequest request);

    AccountResponse toResponse(Account entity);

    void update(@MappingTarget Account entity, AccountRequest request);

    @Mapping(target = "role", ignore = true)
    void updateWithoutRole(@MappingTarget Account entity, AccountRequest request);
}