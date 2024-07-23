package com.bod.bod.challenge.service;

import com.bod.bod.challenge.dto.ChallengeCreateRequestDto;
import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.repository.ChallengeRepository;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeResponseDto createChallenge(ChallengeCreateRequestDto reqDto) {
        Challenge challenge = Challenge.builder()
            .title(reqDto.getTitle())
            .content(reqDto.getContent())
            .image(reqDto.getImage())
            .category(reqDto.getCategory())
            .conditionStatus(reqDto.getConditionStatus())
            .startTime(reqDto.getStartTime())
            .endTime(reqDto.getEndTime())
            .build();

        Challenge savedChallenge = challengeRepository.save(challenge);

        return new ChallengeResponseDto(savedChallenge);
    }

    public List<ChallengeSummaryResponseDto> getChallengesByCategory(int page, Category category) {
        List<ChallengeSummaryResponseDto> challengeList = challengeRepository.getChallengeListByCategory(page, category);
        if(challengeList.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE);
        }
        return challengeList;
    }

    public List<ChallengeSummaryResponseDto> getAllChallenges(int page) {
        List<ChallengeSummaryResponseDto> challengeList = challengeRepository.getChallengeList(page);
        if (challengeList.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE);
        }
        return challengeList;
    }

    public ChallengeResponseDto getChallengeDetails(Long challengeId) {
        Challenge challenge = findChallengeById(challengeId);
        return new ChallengeResponseDto(challenge);
    }

    private Challenge findChallengeById(Long challengeId) {
        return challengeRepository.findChallengeById(challengeId);
    }
}
