package com.bod.bod.user.controller;

import com.bod.bod.global.jwt.security.UserDetailsImpl;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.RefreshToken;
import com.bod.bod.user.service.RefreshTokenServiceImpl;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refresh-token")
@RequiredArgsConstructor
public class RefreshTokenController {

	private final RefreshTokenServiceImpl refreshTokenService;

	@Value("${jwt.refresh-expire-time}")
	private int refreshTokenExpireTime; // 초 단위

	@PostMapping("/create-or-update")
	public ResponseEntity<RefreshToken> createOrUpdateRefreshToken(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam String token
	) {
		User user = userDetails.getUser();
		RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(
			user,
			token,
			LocalDateTime.now().plusSeconds(refreshTokenExpireTime)
		);
		return ResponseEntity.ok(refreshToken);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteRefreshToken(@RequestParam String token) {
		refreshTokenService.deleteByToken(token);
		return ResponseEntity.noContent().build();
	}
}
