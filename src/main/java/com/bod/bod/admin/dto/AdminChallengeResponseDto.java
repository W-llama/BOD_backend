package com.bod.bod.admin.dto;

import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.entity.ConditionStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminChallengeResponseDto {

    private Long challengeId;

    private String title;

    private Category category;

    private ConditionStatus conditionStatus;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String content;

    public AdminChallengeResponseDto(Challenge challenge) {
        this.challengeId = challenge.getId();
        this.title = challenge.getTitle();
        this.category = challenge.getCategory();
        this.conditionStatus = challenge.getConditionStatus();
        this.startTime = challenge.getStartTime();
        this.endTime = challenge.getEndTime();
        this.content = challenge.getContent();
    }
}
