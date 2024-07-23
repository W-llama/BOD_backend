package com.bod.bod.user.service;

import static com.bod.bod.global.jwt.JwtUtil.REFRESH_HEADER;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.refreshToken.RefreshTokenServiceImpl;
import com.bod.bod.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenServiceImpl refreshTokenService;

	@Value("${JWT_SECRET_KEY}")
	private String secretKey;

	@Value("${jwt.refresh-expire-time}")
	private int refreshTokenExpireTime; // 초 단위

	public UserResponseDto signUp(SignUpRequestDto signUpRequestDto) {
		validateEmail(signUpRequestDto.getEmail());

		UserRole userRole = determineUserRole(signUpRequestDto);
		User user = buildUser(signUpRequestDto, userRole);

		userRepository.save(user);
		return new UserResponseDto(user);
	}

	public UserResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
		User user = userRepository.findByUsername(loginRequestDto.getUsername())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

		validatePassword(loginRequestDto.getPassword(), user.getPassword());

		String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getUserRole());
		String refreshToken = jwtUtil.createRefreshToken(user.getUsername());

		refreshTokenService.createOrUpdateRefreshToken(user, refreshToken, LocalDateTime.now().plusSeconds(refreshTokenExpireTime));

		addTokensToResponse(response, accessToken, refreshToken);

		return new UserResponseDto(user);
	}

	private void validateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new GlobalException(ErrorCode.DUPLICATE_EMAIL);
		}
	}

	private void validatePassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new GlobalException(ErrorCode.INVALID_PASSWORD);
		}
	}

	private User buildUser(SignUpRequestDto dto, UserRole role) {
		return User.builder()
			.username(dto.getUsername())
			.email(dto.getEmail())
			.password(passwordEncoder.encode(dto.getPassword()))
			.name(dto.getName())
			.userStatus(UserStatus.ACTIVE)
			.userRole(role)
			.build();
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

	private void addTokensToResponse(HttpServletResponse response, String accessToken, String refreshToken) {
		jwtUtil.addJwtToHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken, response);
		addRefreshTokenCookie(response, refreshToken);
	}

	private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		Cookie refreshTokenCookie = new Cookie(REFRESH_HEADER, refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(refreshTokenExpireTime);

		response.addCookie(refreshTokenCookie);
	}
}
