package com.swpteam.smokingcessation.domain.dto.score;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class ScoreLeaderboardWrapper {
    List<ScoreResponse> leaderboard;
}