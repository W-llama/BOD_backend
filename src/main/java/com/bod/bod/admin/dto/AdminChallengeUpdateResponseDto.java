package com.bod.bod.admin.dto;

import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.entity.ConditionStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminChallengeUpdateResponseDto {

    private Long challengeId;

    private String title;

    private String content;

    private String image;

    private Category category;

    private ConditionStatus conditionStatus;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createAt;

    private LocalDateTime modifiedAt;

    public AdminChallengeUpdateResponseDto(Challenge challenge) {
        this.challengeId = challenge.getId();
        this.title = challenge.getTitle();
        this.content = challenge.getContent();
        this.image = challenge.getImage();
        this.category = challenge.getCategory();
        this.conditionStatus = challenge.getConditionStatus();
        this.startTime = challenge.getStartTime();
        this.endTime = challenge.getEndTime();
        this.createAt = challenge.getCreatedAt();
        this.modifiedAt = challenge.getModifiedAt();
    }

}
