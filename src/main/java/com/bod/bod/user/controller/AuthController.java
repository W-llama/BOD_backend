package com.bod.bod.user.controller;
import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.global.jwt.security.UserDetailsImpl;
import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<CommonResponseDto<Void>> signUp(
		@RequestBody @Valid SignUpRequestDto signUpRequestDto
	) {
		userService.signUp(signUpRequestDto);
		return ResponseEntity.ok().body(new CommonResponseDto<>
			(HttpStatus.CREATED.value(), "회원가입이 완료되었습니다.", null));
	}

	@PostMapping("/login")
	public ResponseEntity<CommonResponseDto<Void>> login(
		@RequestBody @Valid LoginRequestDto loginRequestDto,
		HttpServletResponse response
	) {
		userService.login(loginRequestDto, response);
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "로그인이 완료되었습니다.", null));
	}

	@DeleteMapping("/logout")
	public ResponseEntity<CommonResponseDto<Void>> logout(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		userService.logout(request, response, userDetails.getUser());
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.NO_CONTENT.value(), "로그아웃이 완료되었습니다.", null));
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<CommonResponseDto<Void>> withdraw(
		@RequestParam String username,
		@RequestParam String password,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		HttpServletResponse response
	) {
		userService.withdraw(username, password, userDetails.getUser(), response);
		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "회원탈퇴가 완료되었습니다.", null));
	}
}
