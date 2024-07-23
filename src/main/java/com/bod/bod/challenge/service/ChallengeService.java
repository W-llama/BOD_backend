package com.bod.bod.challenge.service;

import com.bod.bod.challenge.dto.ChallengeCreateRequestDto;
import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeResponseDto createChallenge(ChallengeCreateRequestDto reqDto) {
        Challenge challenge = Challenge.builder()
            .title(reqDto.getTitle())
            .category(reqDto.getCategory())
            .conditionStatus(reqDto.getConditionStatus())
            .startTime(reqDto.getStartTime())
            .endTime(reqDto.getEndTime())
            .build();

        Challenge savedChallenge = challengeRepository.save(challenge);

        return new ChallengeResponseDto(savedChallenge);
    }

}
