package com.bod.bod.admin.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AdminChallengeCreateRequestDto {

    private String title;

    private String content;

    private String category;

    private String conditionStatus;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long limitedUsers;

}
