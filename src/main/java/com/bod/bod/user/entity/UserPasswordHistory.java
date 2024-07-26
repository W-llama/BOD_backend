package com.bod.bod.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "db_user_password_history")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class UserPasswordHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "userId", nullable = false)
	private Long userId;

	@Column(name = "changed_at", nullable = false)
	private LocalDateTime changedAt;

	@PrePersist
	public void prePersist() {
		this.changedAt = LocalDateTime.now();
	}

}
