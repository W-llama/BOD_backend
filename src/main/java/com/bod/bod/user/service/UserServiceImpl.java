package com.bod.bod.user.service;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${JWT_SECRET_KEY}")
	String secretKey;

	public void signUp(SignUpRequestDto signUpRequestDto) {

		if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
			throw new GlobalException(ErrorCode.DUPLICATE_EMAIL);
		}

		UserRole userRole = determineUserRole(signUpRequestDto);

		User user = User.builder()
			.username(signUpRequestDto.getUsername())
			.email(signUpRequestDto.getEmail())
			.password(passwordEncoder.encode(signUpRequestDto.getPassword()))
			.name(signUpRequestDto.getName())
			.userStatus(UserStatus.ACTIVE)
			.userRole(userRole)
			.build();

		userRepository.save(user);
	}

	private UserRole determineUserRole(SignUpRequestDto signUpRequestDto) {
		if ("ADMIN".equalsIgnoreCase(signUpRequestDto.getRole())) {
			validateAdminToken(signUpRequestDto.getAdminToken());
			return UserRole.ADMIN;
		}
		return UserRole.USER;
	}

	private void validateAdminToken(String adminToken) {
		if (adminToken == null || !adminToken.equals(secretKey)) {
			throw new GlobalException(ErrorCode.INVALID_ADMIN_TOKEN);
		}
	}
}
