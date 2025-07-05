package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.tip.TipRequest;
import com.swpteam.smokingcessation.domain.dto.tip.TipResponse;
import com.swpteam.smokingcessation.domain.entity.Tip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TipMapper {
    Tip toEntity(TipRequest tipRequest);

    TipResponse toResponse(Tip tip);
}
