package com.bod.bod.user.service;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.ProfileRequestDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
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
	public void signUp(SignUpRequestDto signUpRequestDto) {
		checkExistingUserOrEmail(signUpRequestDto);
		User user = createUser(signUpRequestDto);
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
		User user = validateLoginRequest(loginRequestDto);
		jwtUtil.issueTokens(user, response, refreshTokenService, refreshTokenExpireTime);
	}

	@Override
	@Transactional
	public void logout(HttpServletRequest request, HttpServletResponse response, User user) {
		String token = jwtUtil.getTokenFromHeader(JwtUtil.AUTHORIZATION_HEADER, request);
		if (token != null) {
			Claims claims = jwtUtil.getUserInfoFromToken(token);
			String tokenUsername = claims.getSubject();

			if (!user.getUsername().equals(tokenUsername)) {
				throw new GlobalException(ErrorCode.INVALID_TOKEN);
			}

			refreshTokenService.deleteByUserId(user.getId());
			jwtUtil.clearAuthToken(response);
		} else {
			throw new GlobalException(ErrorCode.INVALID_TOKEN);
		}
	}

	@Override
	@Transactional
	public void withdraw(LoginRequestDto loginRequestDto, User user, HttpServletResponse response) {
		if (!user.getUsername().equals(loginRequestDto.getUsername())) {
			throw new GlobalException(ErrorCode.INVALID_USERNAME);
		}
		if (user.getUserStatus() == UserStatus.WITHDRAW) {
			throw new GlobalException(ErrorCode.INVALID_USER_STATUS);
		}

		validateUserPassword(loginRequestDto.getPassword(), user.getPassword());

		user.changeUserStatus(UserStatus.WITHDRAW);
		refreshTokenService.deleteByUserId(user.getId());
		jwtUtil.clearAuthToken(response);
		userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponseDto getProfile(User user) {
		return new UserResponseDto(user);
	}

	@Override
	@Transactional
	public UserResponseDto editProfile(ProfileRequestDto profileRequestDto, User user) {
		if (!profileRequestDto.getNickname().equals(user.getNickname())) {
			checkExistingNickname(profileRequestDto.getNickname());
			user.changeNickname(profileRequestDto.getNickname());
		}
		user.changeIntroduce(profileRequestDto.getIntroduce());
		user.changeImage(profileRequestDto.getImage());
		userRepository.save(user);

		return new UserResponseDto(user);
	}

	private User createUser(SignUpRequestDto signUpRequestDto) {
		UserRole userRole = determineUserRole(signUpRequestDto);

		return User.builder()
			.username(signUpRequestDto.getUsername())
			.email(signUpRequestDto.getEmail())
			.password(passwordEncoder.encode(signUpRequestDto.getPassword()))
			.name(signUpRequestDto.getName())
			.nickname(signUpRequestDto.getNickname())
			.introduce(signUpRequestDto.getIntroduce())
			.image(signUpRequestDto.getImage())
			.userStatus(UserStatus.ACTIVE)
			.userRole(userRole)
			.build();
	}

	private void checkExistingUserOrEmail(SignUpRequestDto signUpRequestDto) {
		checkExistingField(userRepository.findByUsername(signUpRequestDto.getUsername()), ErrorCode.ALREADY_USERNAME);
		checkExistingField(userRepository.findByEmail(signUpRequestDto.getEmail()), ErrorCode.DUPLICATE_EMAIL);
	}

	private void checkExistingField(Optional<User> existingField, ErrorCode errorCode) {
		if (existingField.isPresent()) {
			if (existingField.get().getUserStatus() == UserStatus.WITHDRAW) {
				throw new GlobalException(ErrorCode.ALREADY_WITHDRAWN);
			} else {
				throw new GlobalException(errorCode);
			}
		}
	}

	private void checkExistingNickname(String nickname) {
		userRepository.findByNickname(nickname).ifPresent(existingUser -> {
			throw new GlobalException(ErrorCode.ALREADY_NICKNAME);
		});
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

	private User validateLoginRequest(LoginRequestDto loginRequestDto) {
		User user = findActiveUserByUsername(loginRequestDto.getUsername());

		if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
			throw new GlobalException(ErrorCode.INVALID_PASSWORD);
		}

		return user;
	}

	private User findActiveUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

		if (user.getUserStatus() == UserStatus.WITHDRAW) {
			throw new GlobalException(ErrorCode.INVALID_USER_STATUS);
		}

		return user;
	}

	private void validateUserPassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new GlobalException(ErrorCode.INVALID_PASSWORD);
		}
	}


}
