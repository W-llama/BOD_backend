package com.bod.bod.admin.service;

import com.bod.bod.admin.dto.AdminChallengeCreateRequestDto;
import com.bod.bod.admin.dto.AdminChallengeCreateResponseDto;
import com.bod.bod.admin.dto.AdminChallengeUpdateRequestDto;
import com.bod.bod.admin.dto.AdminChallengeUpdateResponseDto;
import com.bod.bod.admin.dto.AdminUserStatusUpdateRequestDto;
import com.bod.bod.admin.dto.AdminUserStatusUpdateResponseDto;
import com.bod.bod.admin.dto.AdminUserUpdateRequestDto;
import com.bod.bod.admin.dto.AdminUserUpdateResponseDto;
import com.bod.bod.admin.dto.AdminUsersResponseDto;
import com.bod.bod.admin.dto.AdminVerificationGetResponse;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.repository.ChallengeRepository;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.repository.UserRepository;
import com.bod.bod.verification.entity.Verification;
import com.bod.bod.verification.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final VerificationRepository verificationRepository;

    public Page<User> getAllUsers(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.findAll(pageable);
    }

    @Transactional
    public AdminUserUpdateResponseDto updateUser(long userId, AdminUserUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

        user.changeName(requestDto.getName());

        return new AdminUserUpdateResponseDto(user);
    }

    @Transactional
    public AdminUserStatusUpdateResponseDto updateUserStatus(long userId, AdminUserStatusUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

        user.changeUserStatus(requestDto.getUserStatus());

        return new AdminUserStatusUpdateResponseDto(user);
    }

    public AdminChallengeCreateResponseDto createChallenge(AdminChallengeCreateRequestDto reqDto) {
        Challenge challenge = Challenge.builder()
            .title(reqDto.getTitle())
            .content(reqDto.getContent())
            .image(reqDto.getImage())
            .category(reqDto.getCategory())
            .conditionStatus(reqDto.getConditionStatus())
            .startTime(reqDto.getStartTime())
            .endTime(reqDto.getEndTime())
            .build();

        Challenge savedChallenge = challengeRepository.save(challenge);

        return new AdminChallengeCreateResponseDto(savedChallenge);
    }

    @Transactional
    public AdminChallengeUpdateResponseDto updateChallenge(long challengeId, AdminChallengeUpdateRequestDto reqDto) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));

        challenge.changeChallenge(reqDto.getTitle(), reqDto.getContent(), reqDto.getImage(), reqDto.getCategory(),
            reqDto.getConditionStatus(), reqDto.getStartTime(), reqDto.getEndTime());

        return new AdminChallengeUpdateResponseDto(challenge);
    }

    @Transactional
    public void deleteChallenge(long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));

        challengeRepository.delete(challenge);
    }

    public Page<Verification> getVerifications(long challengeId, int page, int size, String sortBy, boolean isAsc) {
        challengeRepository.findById(challengeId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CHALLENGE));

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return verificationRepository.findAllByChallengeId(challengeId,pageable);
    }
}
