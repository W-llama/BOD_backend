package com.bod.bod.challenge.dto;

import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.entity.ConditionStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeResponseDto {

    private Long challengeId;
    private String title;
    private String content;
    private String image;
    private Category category;
    private ConditionStatus conditionStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createAt;
    private int completionRate;

    // 기본 생성자
    public ChallengeResponseDto(Challenge challenge) {
        this.challengeId = challenge.getId();
        this.title = challenge.getTitle();
        this.content = challenge.getContent();
        this.image = challenge.getImage();
        this.category = challenge.getCategory();
        this.conditionStatus = challenge.getConditionStatus();
        this.startTime = challenge.getStartTime();
        this.endTime = challenge.getEndTime();
        this.createAt = challenge.getCreatedAt();
    }

    public ChallengeResponseDto(Challenge challenge, int verificationCount) {
        this(challenge);
        this.completionRate = calculateCompletionRate(verificationCount, challenge.getStartTime(), challenge.getEndTime());
    }

    private int calculateCompletionRate(int verificationCount, LocalDateTime startTime, LocalDateTime endTime) {
        long totalDays = java.time.Duration.between(startTime, endTime).toDays();
        if (totalDays <= 0) return 0;
        return (int) Math.round(((double) verificationCount / totalDays) * 100);  // 소수점 반올림하여 정수로 반환
    }
}
