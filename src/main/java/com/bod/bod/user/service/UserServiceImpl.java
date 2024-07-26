package com.bod.bod.user.service;

import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.entity.ConditionStatus;
import com.bod.bod.userChallenge.UserChallenge;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.global.service.S3Service;
import com.bod.bod.user.dto.*;
import com.bod.bod.user.entity.*;
import com.bod.bod.user.repository.*;
import com.bod.bod.userChallenge.UserChallengeRepository;
import com.bod.bod.verification.repository.VerificationRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserPasswordHistoryRepository userPasswordHistoryRepository;
	private final UserChallengeRepository userChallengeRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;
	private final S3Service s3Service;
	private final VerificationRepository verificationRepository;

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
		savePasswordHistory(user, signUpRequestDto.getPassword());
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
		if (token == null) {
			throw new GlobalException(ErrorCode.INVALID_TOKEN);
		}
		Claims claims = jwtUtil.getUserInfoFromToken(token);
		String tokenUsername = claims.getSubject();

		if (!user.getUsername().equals(tokenUsername)) {
			throw new GlobalException(ErrorCode.INVALID_TOKEN);
		}

		refreshTokenService.deleteByUserId(user.getId());
		jwtUtil.clearAuthToken(response);
	}

	@Override
	@Transactional
	public void withdraw(LoginRequestDto loginRequestDto, User user, HttpServletResponse response) {
		validateWithdrawalRequest(loginRequestDto, user);
		user.changeUserStatus(UserStatus.WITHDRAW);
		refreshTokenService.deleteByUserId(user.getId());
		jwtUtil.clearAuthToken(response);
		userRepository.save(user);
	}

	@Override
	public UserResponseDto getMyProfile(User user) {
		validateActiveUserStatus(user);
		return new UserResponseDto(user);
	}

	@Override
	public UserResponseDto getUserprofile(long userId) {
		User user = findById(userId);
		validateActiveUserStatus(user);
		return new UserResponseDto(user);
	}

	@Override
	public Map<String, Slice<ChallengeResponseDto>> getMyChallenges(User user, Pageable pageable) {
		validateActiveUserStatus(user);
		Slice<UserChallenge> userChallengeSlice = userChallengeRepository.findByUser(user, pageable);

		List<ChallengeResponseDto> ongoingChallenges = new ArrayList<>();
		List<ChallengeResponseDto> completedChallenges = new ArrayList<>();

		for (UserChallenge userChallenge : userChallengeSlice) {
			Challenge challenge = userChallenge.getChallenge();
			int verificationCount = verificationRepository.countByChallengeAndUser(challenge, user);
			ChallengeResponseDto challengeResponseDto = new ChallengeResponseDto(challenge, verificationCount);
			if (challenge.getConditionStatus() == ConditionStatus.COMPLETE) {
				completedChallenges.add(challengeResponseDto);
			} else {
				ongoingChallenges.add(challengeResponseDto);
			}
		}

		return getStringSliceMap(
			pageable, ongoingChallenges, completedChallenges);
	}

	@Override
	@Transactional
	public UserResponseDto editProfile(EditProfileRequestDto profileRequestDto, User user) {
		validateActiveUserStatus(user);
		updateProfile(profileRequestDto, user);
		userRepository.save(user);
		return new UserResponseDto(user);
	}

	@Override
	@Transactional
	public UserResponseDto editProfileImage(MultipartFile profileImage, User user) {
		validateActiveUserStatus(user);
		String existingImageUrl = user.getImage();
		String newImageUrl;
		try {
			newImageUrl = s3Service.upload(profileImage);
			if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
				s3Service.deleteFromS3(existingImageUrl);
			}
		} catch (IOException e) {
			throw new GlobalException(ErrorCode.FILE_UPLOAD_ERROR);
		}
		user.changeImage(newImageUrl);
		userRepository.save(user);
		return new UserResponseDto(user);
	}

	@Override
	@Transactional
	public UserResponseDto editPassword(EditPasswordRequestDto editPasswordRequestDto, User user) {
		validateActiveUserStatus(user);
		User userWithHistories = userRepository.findById(user.getId())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

		validateUserPassword(editPasswordRequestDto.getOldPassword(), userWithHistories.getPassword());
		validateNewPassword(editPasswordRequestDto.getNewPassword(), userWithHistories);
		userWithHistories.changePassword(passwordEncoder.encode(editPasswordRequestDto.getNewPassword()));
		savePasswordHistory(userWithHistories, editPasswordRequestDto.getNewPassword());
		return new UserResponseDto(userWithHistories);
	}

	@Override
	public User findById(long userId) {
		return userRepository.findById(userId).
			orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));
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

	private User validateLoginRequest(LoginRequestDto loginRequestDto) {
		User user = findActiveUserByUsername(loginRequestDto.getUsername());
		validateUserPassword(loginRequestDto.getPassword(), user.getPassword());
		return user;
	}

	private User findActiveUserByUsername(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));
		validateActiveUserStatus(user);
		return user;
	}

	private void validateActiveUserStatus(User user) {
		if (user.getUserStatus() == UserStatus.WITHDRAW) {
			throw new GlobalException(ErrorCode.INVALID_USER_STATUS);
		}
	}

	private void validateUserPassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new GlobalException(ErrorCode.INVALID_PASSWORD);
		}
	}

	private void validateWithdrawalRequest(LoginRequestDto loginRequestDto, User user) {
		if (!user.getUsername().equals(loginRequestDto.getUsername())) {
			throw new GlobalException(ErrorCode.INVALID_USERNAME);
		}
		validateActiveUserStatus(user);
		validateUserPassword(loginRequestDto.getPassword(), user.getPassword());
	}

	private void updateProfile(EditProfileRequestDto profileRequestDto, User user) {
		if (!profileRequestDto.getNickname().equals(user.getNickname())) {
			checkExistingNickname(profileRequestDto.getNickname());
			user.changeNickname(profileRequestDto.getNickname());
		}
		user.changeIntroduce(profileRequestDto.getIntroduce());
	}

	private void validateNewPassword(String newPassword, User user) {
		List<UserPasswordHistory> passwordHistories = userPasswordHistoryRepository.findTop3ByUserIdOrderByChangedAtDesc(user.getId());
		for (UserPasswordHistory passwordHistory : passwordHistories) {
			if (passwordEncoder.matches(newPassword, passwordHistory.getPassword())) {
				throw new GlobalException(ErrorCode.INVALID_NEW_PASSWORD);
			}
		}
	}

	private void savePasswordHistory(User user, String password) {
		List<UserPasswordHistory> passwordHistories = userPasswordHistoryRepository.findTop3ByUserIdOrderByChangedAtDesc(user.getId());
		if (passwordHistories.size() >= 3) {
			UserPasswordHistory oldestPasswordHistory = userPasswordHistoryRepository.findByUserIdAndChangedAt(user.getId(),
				passwordHistories.get(2).getChangedAt());

			userPasswordHistoryRepository.delete(oldestPasswordHistory);
		}
		UserPasswordHistory userPasswordHistory = UserPasswordHistory.builder()
			.userId(user.getId())
			.password(passwordEncoder.encode(password))
			.changedAt(LocalDateTime.now())
			.build();

		userPasswordHistoryRepository.save(userPasswordHistory);
	}

	private static Map<String, Slice<ChallengeResponseDto>> getStringSliceMap(Pageable pageable,
		List<ChallengeResponseDto> ongoingChallenges, List<ChallengeResponseDto> completedChallenges) {
		boolean hasNextOngoing = ongoingChallenges.size() == pageable.getPageSize();
		boolean hasNextCompleted = completedChallenges.size() == pageable.getPageSize();

		Slice<ChallengeResponseDto> ongoingChallengesSlice = new SliceImpl<>(ongoingChallenges, pageable, hasNextOngoing);
		Slice<ChallengeResponseDto> completedChallengesSlice = new SliceImpl<>(completedChallenges, pageable, hasNextCompleted);

		Map<String, Slice<ChallengeResponseDto>> result = new HashMap<>();
		result.put("ongoingChallenges", ongoingChallengesSlice);
		result.put("completedChallenges", completedChallengesSlice);
		return result;
	}

}
