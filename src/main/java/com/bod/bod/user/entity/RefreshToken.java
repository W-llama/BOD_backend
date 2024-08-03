package com.bod.bod.user.entity;

import com.bod.bod.global.TimeStamp;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "db_refresh_token")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken extends TimeStamp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String refreshToken;

	@Column(nullable = false)
	private LocalDateTime expirationAt;

	@Column(name = "userId", nullable = false)
	private Long userId;

	public void updateToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void updateExpirationAt(LocalDateTime expirationAt) {
		this.expirationAt = expirationAt;
	}
}
