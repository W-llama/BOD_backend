package com.bod.bod.user.controller;

import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.global.jwt.security.UserDetailsImpl;
import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import com.bod.bod.user.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	public ResponseEntity<CommonResponseDto<UserResponseDto>> signUp(
		@RequestBody @Valid SignUpRequestDto signUpRequestDto
	) {
		UserResponseDto userResponseDto = userService.signUp(signUpRequestDto);
		return ResponseEntity.ok().body(CommonResponseDto.<UserResponseDto>builder()
			.msg("회원 가입이 완료되었습니다.")
			.data(userResponseDto)
			.build());
	}

	@PostMapping("/login")
	public ResponseEntity<CommonResponseDto<UserResponseDto>> login(
		@RequestBody @Valid LoginRequestDto loginRequestDto,
		HttpServletResponse response
	) {
		UserResponseDto userResponseDto = userService.login(loginRequestDto, response);
		return ResponseEntity.ok().body(CommonResponseDto.<UserResponseDto>builder()
			.msg("로그인이 완료되었습니다.")
			.data(userResponseDto)
			.build());
	}

	@DeleteMapping("/logout")
	public ResponseEntity<Void> logout(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		userService.logout(request, response);
		return ResponseEntity.ok().build();
	}
}
