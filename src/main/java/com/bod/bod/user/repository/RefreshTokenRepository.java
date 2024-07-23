package com.bod.bod.user.repository;

import com.bod.bod.user.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUserId(Long userId);

	void deleteByToken(String token);

	void deleteByUserId(Long userId);
}
