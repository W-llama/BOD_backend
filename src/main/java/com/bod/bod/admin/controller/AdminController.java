package com.bod.bod.admin.controller;

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
import com.bod.bod.admin.service.AdminService;
import com.bod.bod.global.dto.CommonResponseDto;
import com.bod.bod.user.entity.User;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admins/users")
    public ResponseEntity<CommonResponseDto<List<AdminUsersResponseDto>>> getAllUsers(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
        @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc
    ) {
        Page<User> responseDto = adminService.getAllUsers(page - 1, size, sortBy, isAsc);

        List<AdminUsersResponseDto> usersList = responseDto.stream()
            .map(AdminUsersResponseDto::new)
            .toList();
        return ResponseEntity.ok().body(new CommonResponseDto<>
            (HttpStatus.OK.value(), "전체 회원 조회에 성공하였습니다!", usersList));
    }

    @PutMapping("/admins/users/{userId}")
    public ResponseEntity<CommonResponseDto<AdminUserUpdateResponseDto>> updateUser(
        @PathVariable(name = "userId") Long userId,
        @Valid @RequestBody AdminUserUpdateRequestDto requestDto
    ) {
        AdminUserUpdateResponseDto adminUserUpdateResponseDto = adminService.updateUser(userId, requestDto);
        return ResponseEntity.ok().body(new CommonResponseDto<>
            (HttpStatus.OK.value(), "회원 정보 수정에 성공하였습니다!", adminUserUpdateResponseDto));
    }

    @PutMapping("/admins/users/{userId}/status")
    public ResponseEntity<CommonResponseDto<AdminUserStatusUpdateResponseDto>> updateUserStatus(
        @PathVariable(name = "userId") Long userId,
        @Valid @RequestBody AdminUserStatusUpdateRequestDto requestDto
    ) {
        AdminUserStatusUpdateResponseDto adminUserStatusUpdateResponseDto = adminService.updateUserStatus(userId, requestDto);
        return ResponseEntity.ok().body(new CommonResponseDto<>
            (HttpStatus.OK.value(), "회원 상태 수정에 성공하였습니다!", adminUserStatusUpdateResponseDto));
    }

    @PostMapping("/admins/challenges")
    public ResponseEntity<CommonResponseDto<AdminChallengeCreateResponseDto>> createChallenge(
        @Valid @RequestBody AdminChallengeCreateRequestDto reqDto) {
        AdminChallengeCreateResponseDto resDto = adminService.createChallenge(reqDto);
        return ResponseEntity.ok().body(new CommonResponseDto<>
            (HttpStatus.OK.value(), "챌린지 등록에 성공하였습니다!", resDto));
    }

    @PatchMapping("/admins/challenges/{challengeId}")
    public ResponseEntity<CommonResponseDto<AdminChallengeUpdateResponseDto>> updateChallenge(
        @PathVariable(name = "challengeId") Long challengeId,
        @Valid @RequestBody AdminChallengeUpdateRequestDto reqDto
    ) {
        AdminChallengeUpdateResponseDto resDto = adminService.updateChallenge(challengeId, reqDto);
        return ResponseEntity.ok().body(new CommonResponseDto<>
            (HttpStatus.OK.value(), "챌린지 수정에 성공하였습니다!", resDto));
    }

    @DeleteMapping("/admins/challenges/{challengeId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteChallenge(
        @PathVariable(name = "challengeId") Long challengeId
    ) {
        adminService.deleteChallenge(challengeId);
        return ResponseEntity.ok().body(new CommonResponseDto<>(HttpStatus.OK.value(), "챌린지 삭제에 성공하였습니다!", null));
    }

    @GetMapping("/admins/challenges/{challengeId}/verifications")
    public ResponseEntity<CommonResponseDto<List<AdminVerificationGetResponse>>> getVerifications(
        @PathVariable(name = "challengeId") Long challengeId,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
        @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc
    ) {
        Page<AdminVerificationGetResponse> responseDto = adminService.getVerifications(challengeId, page - 1, size, sortBy, isAsc);

        List<AdminVerificationGetResponse> verifications = responseDto
            .getContent()
            .stream()
            .toList();
        return ResponseEntity.ok().body(new CommonResponseDto<>
            (HttpStatus.OK.value(), "챌린지 인증 요청 목록 조회에 성공하였습니다!", verifications));
    }
}
