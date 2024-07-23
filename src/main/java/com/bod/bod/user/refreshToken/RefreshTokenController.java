package com.bod.bod.user.refreshToken;

import com.bod.bod.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/refresh-token")
@RequiredArgsConstructor
public class RefreshTokenController {

	private final RefreshTokenServiceImpl refreshTokenService;

	@PostMapping("/create-or-update")
	public ResponseEntity<RefreshToken> createOrUpdateRefreshToken(
		User user,
		@RequestParam String token
	) {
		RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user, token, LocalDateTime.now().plusDays(14));
		return ResponseEntity.ok(refreshToken);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteRefreshToken(@RequestParam String token) {
		refreshTokenService.deleteByToken(token);
		return ResponseEntity.noContent().build();
	}
}
