package com.bod.bod.user.service;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
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
	public void createOrUpdateRefreshToken(User user, String refreshToken, LocalDateTime expirationAt) {
		refreshTokenRepository.findByUserId(user.getId())
			.map(existingToken -> {
				existingToken.updateToken(refreshToken);
				existingToken.updateExpirationAt(expirationAt);
				return refreshTokenRepository.save(existingToken);
			})
			.orElseGet(() -> refreshTokenRepository.save(RefreshToken.builder()
				.userId(user.getId())
				.refreshToken(refreshToken)
				.expirationAt(expirationAt)
				.build()));
	}

	@Override
	@Transactional
	public void deleteByUserId(Long userId) {
		refreshTokenRepository.deleteByUserId(userId);
	}
}


