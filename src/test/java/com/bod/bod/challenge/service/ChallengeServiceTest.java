package com.bod.bod.challenge.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.dto.ChallengeUserListDto;
import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.entity.ConditionStatus;
import com.bod.bod.challenge.repository.ChallengeRepository;
import com.bod.bod.global.dto.PaginationResponse;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.repository.UserRepository;
import com.bod.bod.userchallenge.entity.UserChallenge;
import com.bod.bod.userchallenge.repository.UserChallengeRepository;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {
	@Mock
	private ChallengeRepository challengeRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserChallengeRepository userChallengeRepository;

	@InjectMocks
	private ChallengeService challengeService;

	@ParameterizedTest
	@EnumSource(Category.class)
	@DisplayName("카테고리에 따라 챌린지를 조회한다.")
	void getChallengesByCategory(Category category) {
		// given
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		List<Challenge> testChallenges = IntStream.range(0, size)
			.mapToObj(i -> Challenge.builder()
				.id((long) i)
				.title("Challenge " + i + " - " + category.getCategory())
				.content("Content " + i + " for " + category.getCategory())
				.category(category) // 해당 카테고리로 설정
				.conditionStatus(ConditionStatus.BEFORE)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(30))
				.limitedUsers(100L)
				.joinedUsers(0L)
				.build())
			.toList();

		Page<Challenge> challengePage = new PageImpl<>(testChallenges, pageable, testChallenges.size());
		when(challengeRepository.findByCategory(category, pageable)).thenReturn(challengePage);

		// when
		PaginationResponse<ChallengeSummaryResponseDto> result =
			challengeService.getChallengesByCategory(page, size, category);

		// then
		assertAll("카테고리별 챌린지 조회 검증 - " + category.getCategory(),
			() -> assertNotNull(result),
			() -> assertEquals(size, result.getContent().size()),
			() -> assertEquals(size, result.getTotalElements()),
			() -> assertEquals(size, result.getPageSize()),
			// 카테고리가 올바른지 확인 (제목에 카테고리명이 포함되어 있는지)
			() -> assertTrue(result.getContent().stream()
				.allMatch(challenge -> challenge.getTitle().contains(category.getCategory())))
		);
		verify(challengeRepository, times(1)).findByCategory(category, pageable);
	}

	@Test
	@DisplayName("모든 챌린지를 조회한다.")
	void getAllChallenges() {
		// given
		int page = 0;
		int size = 10;

		List<Challenge> testChallenges = IntStream.range(0, size)
			.mapToObj(i -> Challenge.builder()
				.id((long) i)
				.title("Challenge " + i)
				.content("Content " + i)
				.category(Category.HEALTH)
				.conditionStatus(ConditionStatus.BEFORE)
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now().plusDays(30))
				.limitedUsers(100L)
				.joinedUsers(0L)
				.build())
			.toList();

		Pageable pageable = PageRequest.of(page, size);
		Page<Challenge> challengePage = new PageImpl<>(testChallenges, pageable, testChallenges.size());

		when(challengeRepository.findAll(any(Pageable.class))).thenReturn(challengePage);

		// when
		PaginationResponse<ChallengeSummaryResponseDto> result = challengeService.getAllChallenges(page, size);

		// then
		verify(challengeRepository, times(1)).findAll(any(Pageable.class));
		assertAll("페이지 사이즈 테스트 size=" + size,
			() -> assertNotNull(result),
			() -> assertEquals(size, result.getContent().size()),
			() -> assertEquals(testChallenges.size(), result.getTotalElements()),
			() -> assertEquals(size, result.getPageSize())
		);
	}

	@Test
	@DisplayName("선택한 챌린지의 상세 정보를 조회한다.")
	void getChallengeDetails() {
		// given
		Long challengeId = 1L;
		Challenge testChallenge = Challenge.builder()
			.id(challengeId)
			.title("Test Challenge")
			.content("This is a test challenge.")
			.category(Category.HEALTH)
			.conditionStatus(ConditionStatus.BEFORE)
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusDays(30))
			.limitedUsers(100L)
			.joinedUsers(0L)
			.build();

		// 실제 서비스에서 사용하는 메소드로 변경
		when(challengeRepository.findChallengeById(challengeId))
			.thenReturn(testChallenge);

		// when
		ChallengeResponseDto result = challengeService.getChallengeDetails(challengeId);

		// then
		assertAll("챌린지 상세 정보 조회 검증",
			() -> assertNotNull(result),
			() -> assertEquals(testChallenge.getTitle(), result.getTitle()),
			() -> assertEquals(testChallenge.getContent(), result.getContent()),
			() -> assertEquals(testChallenge.getCategory(), result.getCategory()),
			() -> assertEquals(testChallenge.getConditionStatus(), result.getConditionStatus())
		);
	}

	@Test
	@DisplayName("선택한 챌린지에 현재 유저를 추가한다.")
	void addUserToChallenge() {
		// given
		Long challengeId = 1L;
		Long userId = 1L;

		Challenge testChallenge = Challenge.builder()
			.id(challengeId)
			.title("Test Challenge")
			.content("This is a test challenge.")
			.category(Category.HEALTH)
			.conditionStatus(ConditionStatus.BEFORE)
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusDays(30))
			.limitedUsers(100L)
			.joinedUsers(0L)
			.build();

		User testUser = User.builder()
			.id(userId)
			.email("test@example.com")
			.nickname("testUser")
			.build();

		when(challengeRepository.findByIdWithPessimisticLock(challengeId))
			.thenReturn(Optional.of(testChallenge));
		when(userChallengeRepository.findByUserAndChallenge(testUser, testChallenge))
			.thenReturn(Optional.empty()); // 중복 참가 없음


		// when
		ChallengeResponseDto result = challengeService.addUserToChallenge(challengeId, testUser);

		// then
		assertAll("선택한 챌린지에 테스트유저 추가되었는지 검증",
			() -> assertNotNull(result),
			() -> assertEquals(1, testChallenge.getJoinedUsers()),
			() -> verify(userChallengeRepository, times(1)).save(any(UserChallenge.class))
		);
	}

	@Test
	@DisplayName("선택한 챌린지에 참가한 유저를 조회한다.")
	void getChallengesByUser() {
		//given
		Long challengeId = 1L;
		Long userId = 1L;

		Challenge testChallenge = Challenge.builder()
			.id(challengeId)
			.title("Test Challenge")
			.content("This is a test challenge.")
			.category(Category.HEALTH)
			.conditionStatus(ConditionStatus.BEFORE)
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusDays(30))
			.limitedUsers(100L)
			.joinedUsers(0L)
			.build();

		User testUser = User.builder()
			.id(userId)
			.email("test@example.com")
			.nickname("testUser")
			.build();

		UserChallenge userChallenge = UserChallenge.builder()
			.challenge(testChallenge)
			.user(testUser)
			.build();

		when(challengeRepository.findChallengeById(challengeId)).thenReturn(testChallenge);
		when(userChallengeRepository.findByChallengeId(challengeId)).thenReturn(List.of(userChallenge));

		// when
		List<ChallengeUserListDto> result = challengeService.getChallengesByUser(challengeId);

		//then
		assertAll("선택한 챌린지에 참가한 유저를 조회 검증",
			() -> assertNotNull(result),
			() -> assertEquals(1, result.size())
		);
	}

	@Test
	@DisplayName("선택한 챌린지에 참가한 유저를 삭제한다.")
	void deleteChallenge() {
		//given
		Long challengeId = 1L;
		Long userId = 1L;

		Challenge testChallenge = Challenge.builder()
			.id(challengeId)
			.title("Test Challenge")
			.content("This is a test challenge.")
			.category(Category.HEALTH)
			.conditionStatus(ConditionStatus.BEFORE)
			.startTime(LocalDateTime.now())
			.endTime(LocalDateTime.now().plusDays(30))
			.limitedUsers(100L)
			.joinedUsers(1L)
			.build();

		User testUser = User.builder()
			.id(userId)
			.email("test@example.com")
			.nickname("testUser")
			.build();

		UserChallenge userChallenge = UserChallenge.builder()
			.user(testUser)
			.challenge(testChallenge)
			.build();
		// 실제 서비스에서 사용하는 메소드들로 변경
		when(challengeRepository.findChallengeById(challengeId)).thenReturn(testChallenge);
		when(challengeRepository.findByIdWithPessimisticLock(challengeId))
			.thenReturn(Optional.of(testChallenge));
		when(userChallengeRepository.findByUserAndChallenge(testUser, testChallenge))
			.thenReturn(Optional.of(userChallenge));


		//when
		challengeService.deleteChallenge(challengeId, testUser);

		//then
		verify(userChallengeRepository, times(1)).delete(userChallenge);
		assertEquals(0, testChallenge.getJoinedUsers());
	}

	@ParameterizedTest
	@DisplayName("인기 챌린지 Top 10만 조회되는지 확인한다")
	@ValueSource(ints = {10,20,30})
	void getTop10Challenges_limitTest(int totalCount) {
		// given
		List<ChallengeSummaryResponseDto> allChallenges = IntStream.rangeClosed(1, totalCount)
			.mapToObj(i -> {
				Challenge challenge = Challenge.builder()
					.id((long)i)
					.title("챌린지 " + i)
					.imageUrl("image" + i + ".png")
					.content("내용 " + i)
					.category(Category.HEALTH)
					.limitedUsers(100L)
					.joinedUsers((long)(i * 10))
					.build();
				return new ChallengeSummaryResponseDto(challenge);
			})
			.toList();

		assertEquals(totalCount, allChallenges.size(), "입력 챌린지 개수 확인");

		// 실제 서비스는 Top 10만 반환하도록 동작
		List<ChallengeSummaryResponseDto> top10 = allChallenges.subList(0, Math.min(10, totalCount));

		when(challengeRepository.findTop10ChallengesByUserchallenges()).thenReturn(top10);

		// when
		List<ChallengeSummaryResponseDto> result = challengeService.getTop10Challenges();

		// then
		assertAll("Top 10 챌린지 조회 제한 검증",
			() -> assertNotNull(result),
			() -> assertTrue(result.size() <= 10),
			() -> assertEquals(top10.size(), result.size())
		);
	}

	@Test
	@DisplayName("검색어에 따라 챌린지를 조회한다.")
	void getChallengesBySearch() {
		// given
		String keyword = "헬스";
		int page = 0;
		int size = 5;

		Challenge testChallenge1 = Challenge.builder()
			.id(1L)
			.title("헬스 챌린지")
			.content("건강한 삶")
			.build();

		Challenge testChallenge2 = Challenge.builder()
			.id(2L)
			.title("헬스 챌린지")
			.content("건강한 삶")
			.build();

		Page<Challenge> mockPage = new PageImpl<>(List.of(testChallenge1, testChallenge2));
		when(challengeRepository.findByTitleContaining(eq(keyword), any(Pageable.class)))
			.thenReturn(mockPage);

		// when
		PaginationResponse<ChallengeSummaryResponseDto> result = challengeService.getChallengesBySearch(keyword, page, size);

		// then
		assertAll("검색 결과 챌린지 리스트 검증",
			() -> assertNotNull(result),
			() -> assertEquals(2, result.getContent().size()),
			() -> assertEquals("헬스 챌린지", result.getContent().get(0).getTitle()),
			() -> assertEquals(mockPage.getTotalPages(), result.getTotalPages()),
			() -> assertEquals(mockPage.getNumber(), result.getCurrentPage()),
			() -> assertEquals(mockPage.getSize(), result.getPageSize())
		);
	}
}
