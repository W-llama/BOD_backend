package com.bod.bod.verification.controller;

import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.global.jwt.security.UserDetailsImpl;
import com.bod.bod.verification.dto.VerificationRequestDto;
import com.bod.bod.verification.dto.VerificationResponseDto;
import com.bod.bod.verification.dto.VerificationWithUserResponseDto;
import com.bod.bod.verification.service.VerificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VerificationController {

  private final VerificationService verificationService;

  @PostMapping(value = "/challenges/{challengeId}/verifications")
  public ResponseEntity<CommonResponseDto<VerificationResponseDto>> uploadImage(
	  @PathVariable("challengeId") Long challengeId,
	  @RequestParam(value="image") MultipartFile image,
	  VerificationRequestDto requestDto,
	  @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
	VerificationResponseDto verification = verificationService.requestVerification(challengeId, image, requestDto, userDetails.getUser());
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "챌린지 인증 요청 성공", verification));
  }

  @DeleteMapping("/verifications/{verificationId}")
  public ResponseEntity<CommonResponseDto<Long>> cancelVerification(
	  @PathVariable("verificationId") Long verificationId,
	  @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
	verificationService.cancelVerification(verificationId, userDetails.getUser());
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "챌린지 인증 취소 완료", verificationId));
  }

  @GetMapping("/challenges/{challengeId}/verifications")
  public ResponseEntity<CommonResponseDto<List<VerificationWithUserResponseDto>>> getVerificationsByChallengeId(
	  @RequestParam(value = "page", defaultValue = "1") int page,
	  @PathVariable("challengeId") Long challengeId
  ) {
	List<VerificationWithUserResponseDto> verificationList = verificationService.getVerificationsByChallengeId(page, challengeId);
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "챌린지 인증신청 목록 조회 성공", verificationList));
  }
}


