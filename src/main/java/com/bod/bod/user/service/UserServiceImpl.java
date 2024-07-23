package com.bod.bod.user.service;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	@Value("${JWT_SECRET_KEY}")
	private String secretKey;

	@Value("${jwt.refresh-expire-time}")
	private int refreshTokenExpireTime; // 초 단위

	@Override
	@Transactional
	public UserResponseDto signUp(SignUpRequestDto signUpRequestDto) {
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
		return new UserResponseDto(user);
	}

	@Override
	@Transactional
	public UserResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
		User user = userRepository.findByUsername(loginRequestDto.getUsername())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

		if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
			throw new GlobalException(ErrorCode.INVALID_PASSWORD);
		}

		String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getUserRole());
		String refreshToken = jwtUtil.createRefreshToken(user.getUsername());

		refreshTokenService.createOrUpdateRefreshToken(user, refreshToken, LocalDateTime.now().plusSeconds(refreshTokenExpireTime));

		jwtUtil.addJwtToHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + accessToken, response);
		jwtUtil.addRefreshTokenCookie(response, refreshToken);
		user.login();

		return new UserResponseDto(user);
	}

	@Override
	@Transactional
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		String token = jwtUtil.getTokenFromHeader(JwtUtil.AUTHORIZATION_HEADER, request);
		if (token != null) {
			Claims claims = jwtUtil.getUserInfoFromToken(token);
			String username = claims.getSubject();

			User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

			refreshTokenService.deleteByUserId(user.getId());

			jwtUtil.clearRefreshTokenCookie(response);
			user.logout();
		} else {
			throw new GlobalException(ErrorCode.INVALID_TOKEN);
		}
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
