package com.bod.bod.challenge.controller;

import com.bod.bod.challenge.dto.ChallengeCreateRequestDto;
import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.dto.ChallengeSummaryResponseDto;
import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.service.ChallengeService;
import com.bod.bod.global.dto.CommonResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChallengeController {

  private final ChallengeService challengeService;

  @PostMapping("/admins/challenges")
  public ResponseEntity<CommonResponseDto<ChallengeResponseDto>> createChallenge(@Valid @RequestBody ChallengeCreateRequestDto reqDto) {
	ChallengeResponseDto resDto = challengeService.createChallenge(reqDto);
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "챌린지 등록에 성공하였습니다!", resDto));
  }

  @GetMapping("/challenges/category")
  public ResponseEntity<CommonResponseDto<List<ChallengeSummaryResponseDto>>> getChallengeListByCategory(
	  @RequestParam(value = "page", defaultValue = "1") int page,
	  @RequestParam Category category) {
	List<ChallengeSummaryResponseDto> challengeList = challengeService.getChallengesByCategory(page - 1, category);
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "카테고리별 챌린지 조회 성공", challengeList));
  }

  @GetMapping("/challenges")
  public ResponseEntity<CommonResponseDto<List<ChallengeSummaryResponseDto>>> getAllChallenges(
	  @RequestParam(value = "page", defaultValue = "1") int page) {
	List<ChallengeSummaryResponseDto> challengeList = challengeService.getAllChallenges(page - 1);
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "챌린지 전체 조회 성공", challengeList));
  }

}
