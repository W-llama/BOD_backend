package com.bod.bod.user.service;

import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.RefreshToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenService {

	@Transactional
	void createOrUpdateRefreshToken(User user, String token, LocalDateTime expirationAt);

	void deleteByUserId(Long userId);

}
