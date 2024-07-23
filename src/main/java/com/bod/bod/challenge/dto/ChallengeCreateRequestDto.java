package com.bod.bod.challenge.dto;

import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.ConditionStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeCreateRequestDto {

    private String title;

    private String content;

    private String image;

    private Category category;

    private ConditionStatus conditionStatus;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public ChallengeCreateRequestDto(String title, String content, String image, Category category, ConditionStatus conditionStatus, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.category = category;
        this.conditionStatus = conditionStatus;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
