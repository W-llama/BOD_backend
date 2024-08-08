package com.bod.bod.admin.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.bod.bod.admin.dto.AdminChallengeCreateRequestDto;
import com.bod.bod.admin.dto.AdminChallengeCreateResponseDto;
import com.bod.bod.admin.dto.AdminChallengeResponseDto;
import com.bod.bod.admin.dto.AdminChallengeUpdateRequestDto;
import com.bod.bod.admin.dto.AdminChallengeUpdateResponseDto;
import com.bod.bod.admin.dto.AdminChallengesResponseDto;
import com.bod.bod.admin.dto.AdminUserStatusUpdateRequestDto;
import com.bod.bod.admin.dto.AdminUserStatusUpdateResponseDto;
import com.bod.bod.admin.dto.AdminUserUpdateRequestDto;
import com.bod.bod.admin.dto.AdminUserUpdateResponseDto;
import com.bod.bod.admin.dto.AdminUsersResponseDto;
import com.bod.bod.admin.dto.AdminVerificationGetResponse;
import com.bod.bod.challenge.entity.Category;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.entity.ConditionStatus;
import com.bod.bod.challenge.repository.ChallengeRepository;
import com.bod.bod.global.dto.PaginationResponse;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.FileUploadFailureException;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.repository.UserRepository;
import com.bod.bod.verification.entity.Verification;
import com.bod.bod.verification.repository.VerificationRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final VerificationRepository verificationRepository;

    private final AmazonS3Client amazonS3Client;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET;

    public PaginationResponse<AdminUsersResponseDto> getAllUsers(int page, int size, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAllByOrderByCreatedAtAsc(pageable);

        List<AdminUsersResponseDto> usersDto = users.getContent().stream().map(AdminUsersResponseDto::new).toList();

        return new PaginationResponse<>(
            usersDto,
            users.getTotalPages(),
            users.getTotalElements(),
            users.getNumber(),
            users.getSize()
        );
    }

    @Transactional
    public AdminUserUpdateResponseDto updateUser(long userId, AdminUserUpdateRequestDto requestDto, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

        user.changePoint(requestDto.getPoint());

        return new AdminUserUpdateResponseDto(user);
    }

    @Transactional
    public AdminUserStatusUpdateResponseDto updateUserStatus(long userId, AdminUserStatusUpdateRequestDto requestDto, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

        user.changeUserStatus(requestDto.getUserStatus());

        return new AdminUserStatusUpdateResponseDto(user);
    }

    public AdminChallengeCreateResponseDto createChallenge(MultipartFile image, AdminChallengeCreateRequestDto reqDto, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());
            amazonS3Client.putObject(BUCKET, "challenge/" + image.getOriginalFilename(), image.getInputStream(), metadata);

            String imageUrl = amazonS3Client.getResourceUrl(BUCKET, "challenge/" + image.getOriginalFilename());
            Challenge challenge = Challenge.builder()
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .image(image.getOriginalFilename())
                .imageUrl(imageUrl)
                .category(Category.valueOf(reqDto.getCategory()))
                .conditionStatus(ConditionStatus.valueOf(reqDto.getConditionStatus()))
                .startTime(reqDto.getStartTime())
                .endTime(reqDto.getEndTime())
                .limitedUsers(reqDto.getLimitedUsers())
                .build();
            Challenge savedChallenge = challengeRepository.save(challenge);
            return new AdminChallengeCreateResponseDto(savedChallenge);
        } catch (IOException e) {
            throw new FileUploadFailureException("파일 업로드 실패");
        }
    }

    @Transactional
    public AdminChallengeUpdateResponseDto updateChallenge(long challengeId, AdminChallengeUpdateRequestDto reqDto, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));

        challenge.changeChallenge(reqDto.getTitle(), reqDto.getContent(), reqDto.getCategory(),
            reqDto.getConditionStatus(), reqDto.getStartTime(), reqDto.getEndTime(), reqDto.getLimitedUsers());

        return new AdminChallengeUpdateResponseDto(challenge);
    }

    @Transactional
    public void deleteChallenge(long challengeId, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));
        DeleteObjectRequest request = new DeleteObjectRequest(BUCKET, "challenge/" + challenge.getImage());
        amazonS3Client.deleteObject(request);
        challengeRepository.delete(challenge);
    }

    public PaginationResponse<AdminVerificationGetResponse> getVerifications(long challengeId, int page, int size, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Verification> verifications = verificationRepository.findAllByChallengeIdOrderByCreatedAtAsc(challengeId, pageable);
        if (verifications.isEmpty()) {
            throw new GlobalException(ErrorCode.NOT_FOUND_VERIFICATION);
        }
        List<AdminVerificationGetResponse> verificationResponses = verifications.getContent().stream().map(AdminVerificationGetResponse::new).toList();

        return new PaginationResponse<>(
            verificationResponses,
            verifications.getTotalPages(),
            verifications.getTotalElements(),
            verifications.getNumber(),
            verifications.getSize()
        );
    }

    @Transactional
    public void approveVerification(long verificationId, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }
        Verification verification = verificationRepository.findVerificationById(verificationId);
        if (verification.getStatus().getStatus().equals("APPROVE")) {
            throw new GlobalException(ErrorCode.ALREADY_EXISTS_APPROVE_VERIFICATION);
        } else {
            verification.changeStatusApprove();
            User verifiedUser = verification.getUser();
            verifiedUser.increasePoint();
            redisTemplate.opsForZSet().add("ranking", verifiedUser.getNickname(), verifiedUser.getPoint());
        }
    }

    @Transactional
    public void rejectVerification(long verificationId, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }
        Verification verification = verificationRepository.findVerificationById(verificationId);
        if (verification.getStatus().getStatus().equals("REJECT")) {
            throw new GlobalException(ErrorCode.ALREADY_EXISTS_REJECT_VERIFICATION);
        } else {
            if (verification.getStatus().getStatus().equals("APPROVE")) {
                verification.changeStatusReject();
                verification.getUser().decreasePoint();
            } else {
                verification.changeStatusReject();
            }
        }
    }

    public PaginationResponse<AdminChallengesResponseDto> getAllChallenges(int page, int size, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Challenge> challenges = challengeRepository.findAllByOrderByCreatedAtAsc(pageable);
        List<AdminChallengesResponseDto> challengesResponseDtos = challenges.getContent().stream().map(AdminChallengesResponseDto::new).toList();

        return new PaginationResponse<>(
            challengesResponseDtos,
            challenges.getTotalPages(),
            challenges.getTotalElements(),
            challenges.getNumber(),
            challenges.getSize()
        );
    }

    public AdminChallengeResponseDto getChallenge(long challengeId, User loginUser) {
        if (!loginUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new GlobalException(ErrorCode.USER_ACCESS_DENIED);
        }
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));

        return new AdminChallengeResponseDto(challenge);
    }
}
