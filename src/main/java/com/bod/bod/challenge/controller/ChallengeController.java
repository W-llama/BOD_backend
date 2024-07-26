package com.bod.bod.challenge.controller;

import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.dto.ChallengeUserListDto;
import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.service.ChallengeService;
import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.global.jwt.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChallengeController {

	private final ChallengeService challengeService;

	@GetMapping("/challenges/category")
	public ResponseEntity<CommonResponseDto<List<ChallengeSummaryResponseDto>>> getChallengeListByCategory(
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam Category category
	) {
		List<ChallengeSummaryResponseDto> challengeList = challengeService.getChallengesByCategory(
			page - 1, category);
		return ResponseEntity.ok().body(new CommonResponseDto<>
			(HttpStatus.OK.value(), "카테고리별 챌린지 조회 성공", challengeList));
	}

	@GetMapping("/challenges")
	public ResponseEntity<CommonResponseDto<List<ChallengeSummaryResponseDto>>> getAllChallenge(
		@RequestParam(value = "page", defaultValue = "1") int page
	) {
		List<ChallengeSummaryResponseDto> challengeList = challengeService.getAllChallenges(
			page - 1);
		return ResponseEntity.ok().body(new CommonResponseDto<>
			(HttpStatus.OK.value(), "챌린지 전체 조회 성공", challengeList));
	}

	@GetMapping("/challenges/{challengeId}")
	public ResponseEntity<CommonResponseDto<ChallengeResponseDto>> getChallengeDetails(
		@PathVariable("challengeId") Long challengeId
	) {
		ChallengeResponseDto challenge = challengeService.getChallengeDetails(challengeId);
		return ResponseEntity.ok().body(new CommonResponseDto<>
			(HttpStatus.OK.value(), "챌린지 단건 조회 성공", challenge));
	}

	@PostMapping("/challenges/{challengeId}")
	public ResponseEntity<CommonResponseDto<ChallengeResponseDto>> addChallenge(
		@PathVariable("challengeId") Long challengeId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		//로그인 한 유저정보
		String username = userDetails.getUsername();
		ChallengeResponseDto challenge = challengeService.addUserToChallenge(challengeId, username);

		return ResponseEntity.ok().body(new CommonResponseDto<>
			(HttpStatus.OK.value(), "챌린지 등록 성공", challenge));
	}

	@GetMapping("/challenges/{challengeId}/users")
	public ResponseEntity<CommonResponseDto<List<ChallengeUserListDto>>>getUserByChallenges(
		@PathVariable("challengeId") Long challengeId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		String username = userDetails.getUsername();
		List<ChallengeUserListDto> userList = challengeService.getChallengesByUser(challengeId, username);

		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "선택한 챌린지의 유저 조회 성공", userList
		));
	}
}
