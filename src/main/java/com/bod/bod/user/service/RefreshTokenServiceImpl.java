package com.bod.bod.user.service;

import com.bod.bod.user.entity.RefreshToken;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	@Transactional
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

	@Override
	@Transactional
	public void deleteByToken(String token) {
		refreshTokenRepository.deleteByToken(token);
	}

	@Override
	@Transactional
	public void deleteByUserId(Long userId) {
		refreshTokenRepository.deleteByUserId(userId);
	}

	@Override
	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	@Override
	public Optional<RefreshToken> findByUserId(Long userId) {
		return refreshTokenRepository.findByUserId(userId);
	}
}
