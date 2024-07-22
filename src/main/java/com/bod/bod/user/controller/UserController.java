package com.bod.bod.user.controller;

import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserServiceImpl userService;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponseDto> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
		userService.signUp(signUpRequestDto);

		return ResponseEntity.ok(CommonResponseDto.builder()
			.msg("회원 가입이 완료되었습니다")
			.build());
	}
}
