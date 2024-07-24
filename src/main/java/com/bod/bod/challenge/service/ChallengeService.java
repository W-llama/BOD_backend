package com.bod.bod.challenge.service;

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

    public List<ChallengeSummaryResponseDto> getChallengesByCategory(int page, Category category) {
        List<ChallengeSummaryResponseDto> challengeList = challengeRepository.getChallengeListByCategory(page, category);
        if (challengeList.isEmpty()) {
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

    public Challenge findChallengeById(Long challengeId) {
        return challengeRepository.findChallengeById(challengeId);
    }
}
