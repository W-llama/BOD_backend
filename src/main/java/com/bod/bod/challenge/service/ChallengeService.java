package com.bod.bod.challenge.service;

import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.dto.ChallengeUserListDto;
import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.repository.ChallengeRepository;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.repository.UserRepository;
import com.bod.bod.userchallenge.entity.UserChallenge;
import com.bod.bod.userchallenge.repository.UserChallengeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final UserChallengeRepository userChallengeRepository;

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
        Challenge challenge = findById(challengeId);
        return new ChallengeResponseDto(challenge);
    }

    public ChallengeResponseDto addUserToChallenge(Long challengeId, String username){

        User user = userRepository.findByUsername(username)
            .orElseThrow(()-> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

        Challenge challenge = challengeRepository.findChallengeById(challengeId);

        Optional<UserChallenge> existingUserChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge);
        if(existingUserChallenge.isPresent()){
            throw new GlobalException(ErrorCode.DUPLICATE_CHALLENGE);
        }
        UserChallenge userChallenge = UserChallenge.builder()
            .user(user)
            .challenge(challenge)
            .build();
        userChallengeRepository.save(userChallenge);
        return new ChallengeResponseDto(challenge);
    }

    public List<ChallengeUserListDto> getChallengesByUser(Long challengeId, String username) {

        Challenge challenge = challengeRepository.findChallengeById(challengeId);
        List<UserChallenge> userChallenges = userChallengeRepository.findByChallengeId(challenge.getId());

        return userChallenges.stream()
            .map(userChallenge -> new ChallengeUserListDto(challenge, userChallenge.getUser()))
            .toList();
    }

    public void deleteChallenge(long challengeId, User user) {
        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, findById(challengeId))
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USER_CHALLENGE));

        userChallengeRepository.delete(userChallenge);
    }

    public Challenge findById(Long challengeId) {
        return challengeRepository.findChallengeById(challengeId);
    }
}
