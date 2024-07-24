package com.bod.bod.admin.service;

import com.bod.bod.admin.dto.AdminChallengeCreateRequestDto;
import com.bod.bod.admin.dto.AdminChallengeCreateResponseDto;
import com.bod.bod.admin.dto.AdminUserStatusUpdateRequestDto;
import com.bod.bod.admin.dto.AdminUserStatusUpdateResponseDto;
import com.bod.bod.admin.dto.AdminUserUpdateRequestDto;
import com.bod.bod.admin.dto.AdminUserUpdateResponseDto;
import com.bod.bod.admin.dto.AdminUsersResponseDto;
import com.bod.bod.challenge.dto.ChallengeCreateRequestDto;
import com.bod.bod.challenge.dto.ChallengeResponseDto;
import com.bod.bod.challenge.entity.Challenge;
import com.bod.bod.challenge.repository.ChallengeRepository;
import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.repository.UserRepository;
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

    public Page<AdminUsersResponseDto> getAllUsers(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction,sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userList;
        userList = userRepository.findAll(pageable);

        return userList.map(AdminUsersResponseDto::new);
    }

    @Transactional
    public AdminUserUpdateResponseDto updateUser(long userId, AdminUserUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

        user.setName(requestDto.getName());

        return new AdminUserUpdateResponseDto(user);
    }

    @Transactional
    public AdminUserStatusUpdateResponseDto updateUserStatus(long userId, AdminUserStatusUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

        user.setUserStatus(requestDto.getUserStatus());

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
}
