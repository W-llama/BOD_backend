package com.bod.bod.challenge.service;

import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.dto.ChallengeUserListDto;
import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.entity.ConditionStatus;
import com.bod.bod.challenge.repository.ChallengeRepository;
import com.bod.bod.global.dto.PaginationResponse;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.repository.UserRepository;
import com.bod.bod.userchallenge.entity.UserChallenge;
import com.bod.bod.userchallenge.repository.UserChallengeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengeService {

  private final ChallengeRepository challengeRepository;
  private final UserRepository userRepository;
  private final UserChallengeRepository userChallengeRepository;

  @Transactional(readOnly = true)
  public PaginationResponse<ChallengeSummaryResponseDto> getChallengesByCategory(int page, int size, Category category) {
	Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
	Page<Challenge> challengesByCategory = challengeRepository.findByCategory(category, pageable);
	if (challengesByCategory.isEmpty()) {
	  throw new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE);
	}
	List<ChallengeSummaryResponseDto> challengeList = challengesByCategory.getContent().stream()
		.map(ChallengeSummaryResponseDto::new)
		.toList();

	return new PaginationResponse<>(
		challengeList,
		challengesByCategory.getTotalPages(),
		challengesByCategory.getTotalElements(),
		challengesByCategory.getNumber(),
		challengesByCategory.getSize()
	);
  }

  @Transactional(readOnly = true)
  public PaginationResponse<ChallengeSummaryResponseDto> getAllChallenges(int page, int size) {
	Pageable pageable = PageRequest.of(page, size);
	Page<Challenge> challenges = challengeRepository.findAll(pageable);
	if (challenges.isEmpty()) {
	  throw new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE);
	}
	List<ChallengeSummaryResponseDto> challengeList = challenges.getContent().stream()
		.map(ChallengeSummaryResponseDto::new)
		.toList();

	return new PaginationResponse<>(
		challengeList,
		challenges.getTotalPages(),
		challenges.getTotalPages(),
		challenges.getNumber(),
		challenges.getSize()
	);
  }

  @Transactional(readOnly = true)
  public ChallengeResponseDto getChallengeDetails(Long challengeId) {
	Challenge challenge = findById(challengeId);
	return new ChallengeResponseDto(challenge);
  }

  @Transactional
  public ChallengeResponseDto addUserToChallenge(Long challengeId, User user) {
	Challenge challenge = challengeRepository.findByIdWithPessimisticLock(challengeId)
		.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));
	if (challenge.getConditionStatus().equals(ConditionStatus.COMPLETE)) {
	  throw new GlobalException(ErrorCode.COMPLETE_CHALLENGE);
	}
	Optional<UserChallenge> existingUserChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge);
	if (existingUserChallenge.isPresent()) {
	  throw new GlobalException(ErrorCode.DUPLICATE_CHALLENGE);
	}
	if (challenge.getJoinedUsers() >= challenge.getLimitedUsers()) {
	  throw new GlobalException(ErrorCode.LIMIT_FULL_CHALLENGE);
	} else {
	  challenge.increaseJoinedUsers();
	}
	UserChallenge userChallenge = UserChallenge.builder()
		.user(user)
		.challenge(challenge)
		.build();
	userChallengeRepository.save(userChallenge);
	return new ChallengeResponseDto(challenge);
  }

  @Transactional(readOnly = true)
  public List<ChallengeUserListDto> getChallengesByUser(Long challengeId) {

	Challenge challenge = challengeRepository.findChallengeById(challengeId);
	List<UserChallenge> userChallenges = userChallengeRepository.findByChallengeId(challenge.getId());

	return userChallenges.stream()
		.map(userChallenge -> new ChallengeUserListDto(challenge, userChallenge.getUser()))
		.toList();
  }

  @Transactional
  public void deleteChallenge(long challengeId, User user) {
	UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, findById(challengeId))
		.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USER_CHALLENGE));

	Challenge challenge = challengeRepository.findByIdWithPessimisticLock(challengeId)
		.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));
	challenge.decreaseJoinedUsers();

	userChallengeRepository.delete(userChallenge);
  }

  @Transactional(readOnly = true)
  public List<ChallengeSummaryResponseDto> getTop10Challenges() {
	List<ChallengeSummaryResponseDto> top10ChallengeList = challengeRepository.findTop10ChallengesByUserchallenges();
	if (top10ChallengeList.isEmpty()) {
	  throw new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE);
	}
	return top10ChallengeList;
  }

  @Transactional(readOnly = true)
  public PaginationResponse<ChallengeSummaryResponseDto> getChallengesBySearch(String title, int page, int size) {
	Pageable pageable = PageRequest.of(page, size);

	if(title == null) title = "";

	Page<Challenge> challengeListBySearch = challengeRepository.findByTitleContaining(title, pageable);
	if (challengeListBySearch.isEmpty()) {
	  throw new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE);
	}

	List<ChallengeSummaryResponseDto> challengeList = challengeListBySearch.getContent().stream()
		.map(challenge -> new ChallengeSummaryResponseDto(challenge)).toList();

	return new PaginationResponse<>(
		challengeList,
		challengeListBySearch.getTotalPages(),
		challengeListBySearch.getTotalPages(),
		challengeListBySearch.getNumber(),
		challengeListBySearch.getSize()
	);
  }

  public Challenge findById(Long challengeId) {
	return challengeRepository.findChallengeById(challengeId);
  }
}
