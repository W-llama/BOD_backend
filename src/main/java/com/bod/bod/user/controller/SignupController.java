package com.bod.bod.user.controller;

import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.user.dto.EmailAddressDto;
import com.bod.bod.user.dto.EmailAuthenticationDto;
import com.bod.bod.user.service.SignupService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/signup")
public class SignupController {

  private final SignupService signUpService;

  @PostMapping("/check-username")
  public ResponseEntity<CommonResponseDto<Boolean>> checkUsername(
	  @RequestBody Map<String, String> request
  ) {
	boolean exists = signUpService.checkUsernameExists(request.get("username"));
	return ResponseEntity.ok()
		.body(new CommonResponseDto<>(
			HttpStatus.OK.value(), exists ? "아이디가 이미 존재합니다!" : "아이디가 사용 가능합니다!", exists));
  }

  @PostMapping("/check-email")
  public ResponseEntity<CommonResponseDto<Boolean>> checkEmail(
	  @RequestBody Map<String, String> request
  ) {
	boolean exists = signUpService.checkEmailExists(request.get("email"));
	return ResponseEntity.ok()
		.body(new CommonResponseDto<>(HttpStatus.OK.value(), exists ? "이메일이 이미 존재합니다." : "이메일이 사용 가능합니다.", exists));
  }

  @PostMapping("/check-nickname")
  public ResponseEntity<CommonResponseDto<Boolean>> checkNickname(
	  @RequestBody Map<String, String> request
  ) {
	boolean exists = signUpService.checkNicknameExists(request.get("nickname"));
	return ResponseEntity.ok()
		.body(new CommonResponseDto<>(HttpStatus.OK.value(), exists ? "닉네임이 이미 존재합니다." : "닉네임이 사용 가능합니다.", exists));
  }

  @PostMapping("/validate-admin-token")
  public ResponseEntity<CommonResponseDto<Boolean>> validateAdminToken(
	  @RequestBody Map<String, String> request
  ) {
	boolean valid = signUpService.checkAdminToken(request.get("adminToken"));
	return ResponseEntity.ok()
		.body(new CommonResponseDto<>(HttpStatus.OK.value(), valid ? "유효한 어드민 토큰입니다." : "유효하지 않은 어드민 토큰입니다.", valid));
  }

  @PostMapping("/email")
  public ResponseEntity<CommonResponseDto<EmailAddressDto>> sendAuthenticationCode(
	  @RequestBody @Valid EmailAddressDto emailAddressDto
  ) throws MessagingException {
	EmailAddressDto addressDto = signUpService.sendAuthenticationCode(emailAddressDto.getEmail());
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "인증번호 전송 완료", addressDto));
  }

  @PostMapping("/email/verify")
  public ResponseEntity<CommonResponseDto<String>> emailAuthentication(
	  @RequestBody @Valid EmailAuthenticationDto emailAuthenticationDto
  ) {
	signUpService.emailAuthentication(emailAuthenticationDto.getEmail(), emailAuthenticationDto.getCode());
	return ResponseEntity.ok().body(new CommonResponseDto<>
		(HttpStatus.OK.value(), "이메일 인증번호 검증 완료", null));
  }
}
