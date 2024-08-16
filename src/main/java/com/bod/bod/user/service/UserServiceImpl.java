package com.bod.bod.user.service;

import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.global.service.S3Service;
import com.bod.bod.user.dto.EditIntroduceRequestDto;
import com.bod.bod.user.dto.EditPasswordRequestDto;
import com.bod.bod.user.dto.EditNickNameRequestDto;
import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.PointRankingResponseDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserPasswordHistory;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.repository.UserPasswordHistoryRepository;
import com.bod.bod.user.repository.UserRepository;
import com.bod.bod.userchallenge.entity.UserChallenge;
import com.bod.bod.userchallenge.repository.UserChallengeRepository;
import com.bod.bod.verification.entity.Status;
import com.bod.bod.verification.repository.VerificationRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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

  private final RedisTemplate<String, String> redisTemplate;

  @Value("${jwt.secret.key}")
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
  public void withdraw(String username, String password, User user, HttpServletResponse response) {
	validateWithdrawalRequest(username, password, user);
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

	List<ChallengeResponseDto> beforeChallenges = new ArrayList<>();
	List<ChallengeResponseDto> ongoingChallenges = new ArrayList<>();
	List<ChallengeResponseDto> completedChallenges = new ArrayList<>();

	userChallengeSlice.forEach(userChallenge -> {

	  Challenge challenge = userChallenge.getChallenge();
	  int verificationApprovedCount = verificationRepository.countByChallengeAndUserAndStatus(challenge, user, Status.APPROVE);
	  ChallengeResponseDto challengeResponseDto = new ChallengeResponseDto(challenge, verificationApprovedCount);

	  switch (challenge.getConditionStatus()) {
		case COMPLETE:
		  completedChallenges.add(challengeResponseDto);
		  break;
		case TODO:
		  ongoingChallenges.add(challengeResponseDto);
		  break;
		case BEFORE:
		  beforeChallenges.add(challengeResponseDto);
		  break;
	  }
	});

	return createChallengeSlices(pageable, ongoingChallenges, completedChallenges, beforeChallenges);
  }

  @Override
  @Transactional
  public UserResponseDto editNickName(EditNickNameRequestDto editNickNameRequestDto, User user) {
	validateActiveUserStatus(user);
	updateNickName(editNickNameRequestDto, user);
	userRepository.save(user);
	return new UserResponseDto(user);
  }

  @Override
  @Transactional
  public UserResponseDto editIntroduce(EditIntroduceRequestDto editIntroduceRequestDto, User user) {
	  updateIntroduce(editIntroduceRequestDto, user);
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
  public void validateNewPassword(String newPassword, User user) {
	List<UserPasswordHistory> passwordHistories = userPasswordHistoryRepository.findTop3ByUserIdOrderByChangedAtDesc(user.getId());
	for (UserPasswordHistory passwordHistory : passwordHistories) {
	  if (passwordEncoder.matches(newPassword, passwordHistory.getPassword())) {
		throw new GlobalException(ErrorCode.INVALID_NEW_PASSWORD);
	  }
	}
  }

  @Override
  public void validateUsernameRequest(String username, User user) {
	if (!user.getUsername().equals(username)) {
	  throw new GlobalException(ErrorCode.INVALID_USERNAME);
	}
  }

  @Override
  public List<PointRankingResponseDto> getRankingList() {
	String key = "ranking";
	try{
	ZSetOperations<String, String> stringStringZSetOperations = redisTemplate.opsForZSet();
	Set<ZSetOperations.TypedTuple<String>> typedTuples = stringStringZSetOperations.reverseRangeWithScores(key, 0, 10);
	  if (typedTuples.isEmpty()) {
		throw new GlobalException(ErrorCode.EMPTY_POINT_RANKING_LIST);
	  }
	List<PointRankingResponseDto> rankingList = typedTuples.stream()
		.map(tuple -> new PointRankingResponseDto(tuple.getValue(), tuple.getScore()))
		.toList();
	return sortRanks(rankingList);
	} catch (RedisConnectionFailureException e) {
	  throw new GlobalException(ErrorCode.REDIS_CONNECTION_FAILED);
	}
  }

  @Override
  public User findById(long userId) {
	return userRepository.findById(userId)
		.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));
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
	if (StringUtils.hasText(signUpRequestDto.getAdminToken())) {
	  validateAdminToken(signUpRequestDto.getAdminToken());
	  return UserRole.ADMIN;
	}
	return UserRole.USER;
  }

  private void validateAdminToken(String adminToken) {
	if (!adminToken.equals(secretKey)) {
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

  private void validateWithdrawalRequest (String username, String password, User user) {
	validateUsernameRequest(username, user);
	validateActiveUserStatus(user);
	validateUserPassword(password, user.getPassword());
  }

  private void updateNickName (EditNickNameRequestDto editNickNameRequestDto, User user) {
	if (!editNickNameRequestDto.getNickname().equals(user.getNickname())) {
	  checkExistingNickname(editNickNameRequestDto.getNickname());
	  user.changeNickname(editNickNameRequestDto.getNickname());
	}
  }

  private void updateIntroduce(EditIntroduceRequestDto editIntroduceRequestDto, User user) {
	  user.changeIntroduce(editIntroduceRequestDto.getIntroduce());
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

  private static Slice<ChallengeResponseDto> createSlice(List<ChallengeResponseDto> challenges, Pageable pageable) {
	boolean hasNext = challenges.size() == pageable.getPageSize();
	return new SliceImpl<>(challenges, pageable, hasNext);
  }

  private static Map<String, Slice<ChallengeResponseDto>> createChallengeSlices(
	  Pageable pageable,
	  List<ChallengeResponseDto> ongoingChallenges,
	  List<ChallengeResponseDto> completedChallenges,
	  List<ChallengeResponseDto> beforeChallenges
  ) {
	return Map.of(
		"ongoingChallenges", createSlice(ongoingChallenges, pageable),
		"completedChallenges", createSlice(completedChallenges, pageable),
		"beforeChallenges", createSlice(beforeChallenges, pageable)
	);
  }

  private List<PointRankingResponseDto> sortRanks(List<PointRankingResponseDto> rankingList) {
	List<PointRankingResponseDto> rankedList = new ArrayList<>();
	int rank = 1;
	int currentRank = 1;
	long beforePoint = 0;

	for (PointRankingResponseDto dto : rankingList) {
	  if (dto.getPoint() != beforePoint) {
		rank = currentRank;
	  }
	  dto.setRank(rank);
	  beforePoint = dto.getPoint();
	  currentRank++;
	  rankedList.add(dto);
	}
	return rankedList.stream().limit(5).toList();
  }
}
