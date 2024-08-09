package com.bod.bod.user.controller;

import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.user.service.OAuth2CallbackServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2CallbackController {

	private final OAuth2CallbackServiceImpl oAuth2CallbackService;

	@PostMapping("/naver")
	public ResponseEntity<CommonResponseDto<Void>> naverCallback(
		@RequestBody Map<String, String> params,
		HttpServletResponse httpServletResponse
	) {
		oAuth2CallbackService.handleNaverLogin(params, httpServletResponse);

		return ResponseEntity.ok().body(new CommonResponseDto<>(
			HttpStatus.OK.value(), "네이버 로그인이 완료되었습니다.", null));
	}
}
