package com.bod.bod.admin.dto;

import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.ConditionStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminChallengeUpdateRequestDto {

    private String title;

    private String content;

    private Category category;

    private ConditionStatus conditionStatus;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
