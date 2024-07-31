package com.bod.bod.global.jwt;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.RefreshToken;
import com.bod.bod.user.service.RefreshTokenService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	public String refreshToken(String refreshToken) {
		Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshToken);

		if (refreshTokenOptional.isPresent()) {
			RefreshToken token = refreshTokenOptional.get();
			if (jwtUtil.validateToken(refreshToken)) {
				return jwtUtil.refreshAccessToken(refreshToken);
			} else {
				throw new GlobalException(ErrorCode.EXPIRED_REFRESH_TOKEN);
			}
		} else {
			throw new GlobalException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}
}
