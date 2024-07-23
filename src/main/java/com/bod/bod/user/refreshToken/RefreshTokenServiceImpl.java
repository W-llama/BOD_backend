package com.bod.bod.user.refreshToken;

import com.bod.bod.user.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl {

	private final RefreshTokenRepository refreshTokenRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createOrUpdateRefreshToken(User user, String token, LocalDateTime expirationAt) {
		Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(user.getId());
		if (existingToken.isPresent()) {
			RefreshToken refreshToken = existingToken.get();
			refreshToken.updateToken(token);
			refreshToken.updateExpirationAt(expirationAt);
			return refreshTokenRepository.save(refreshToken);
		} else {
			RefreshToken refreshToken = RefreshToken.builder()
				.user(user)
				.token(token)
				.expirationAt(expirationAt)
				.build();
			return refreshTokenRepository.save(refreshToken);
		}
	}

	public void deleteByToken(String token) {
		refreshTokenRepository.deleteByToken(token);
	}

	public Optional<RefreshToken> findByUserId(Long userId) {
		return refreshTokenRepository.findByUserId(userId);
	}
}
