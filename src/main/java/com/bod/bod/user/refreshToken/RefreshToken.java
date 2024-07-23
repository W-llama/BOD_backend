package com.bod.bod.user.refreshToken;

import com.bod.bod.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "refresh_token")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String token;

	@Column(nullable = false)
	private LocalDateTime expirationAt;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public void updateToken(String token) {
		this.token = token;
	}

	public void updateExpirationAt(LocalDateTime expirationAt) {
		this.expirationAt = expirationAt;
	}
}
