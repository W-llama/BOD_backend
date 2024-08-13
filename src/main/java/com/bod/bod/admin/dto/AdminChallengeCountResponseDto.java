package com.bod.bod.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminChallengeCountResponseDto {

    private Long challengesCount;

    private Long beforeChallengesCount;

    private Long todoChallengesCount;

    private Long completeChallengesCount;

    public AdminChallengeCountResponseDto(long challengesCount, long beforeChallengesCount, long todoChallengesCount, long completeChallengesCount) {
        this.challengesCount = challengesCount;
        this.beforeChallengesCount = beforeChallengesCount;
        this.todoChallengesCount = todoChallengesCount;
        this.completeChallengesCount = completeChallengesCount;
    }


}
