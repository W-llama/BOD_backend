package com.bod.bod.verification.controller;

import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.verification.dto.VerificationRequestDto;
import com.bod.bod.verification.dto.VerificationResponseDto;
import com.bod.bod.verification.service.VerificationService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VerificationController {

  private final VerificationService verificationService;

  @PostMapping(value = "/users/challenges/{challengeId}/verifications")
  public ResponseEntity<CommonResponseDto<VerificationResponseDto>> uploadImage(@PathVariable("challengeId") Long challengeId, @RequestParam(value="image") MultipartFile image, @Valid @RequestBody VerificationRequestDto requestDto) throws IOException {
	String imageName = image.getOriginalFilename();
	VerificationResponseDto verification = verificationService.requestVerification(challengeId, image, imageName, requestDto);
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "챌린지 인증 요청 성공", verification));
  }

//  @DeleteMapping("/users/challenges/{challengeId}/verifications/{verificationId}")
//  public ResponseEntity<String> cancelVerification(@PathVariable String imageName) {
//	verificationService.cancelVerification(imageName);
//	return ResponseEntity.status(HttpStatus.OK).body("인증 취소 완료");
//  }


}
