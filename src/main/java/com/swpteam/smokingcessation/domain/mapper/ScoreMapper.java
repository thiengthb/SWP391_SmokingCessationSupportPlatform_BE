package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.score.ScoreResponse;
import com.swpteam.smokingcessation.domain.entity.Score;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoreMapper {

    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "avatar", source = "account.avatar")
    @Mapping(target = "score_rank", source = "rank")
    ScoreResponse toResponse(Score entity);
}
