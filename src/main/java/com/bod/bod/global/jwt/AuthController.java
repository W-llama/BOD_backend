package com.bod.bod.global.jwt;

import com.bod.bod.global.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/refresh-token")
	public ResponseEntity<CommonResponseDto<JwtResponseDto>> refreshToken(
		@CookieValue(value = "Refresh") String refreshToken
	) {
		System.out.println(refreshToken);
		String newAccessToken = authService.refreshToken(refreshToken);
		return ResponseEntity.ok(new CommonResponseDto<>(
			HttpStatus.OK.value(), "Token refreshed successfully.", new JwtResponseDto(newAccessToken)));
	}
}
