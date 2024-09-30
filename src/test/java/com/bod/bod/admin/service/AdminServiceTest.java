package com.bod.bod.admin.service;

import com.bod.bod.admin.dto.AdminUsersResponseDto;
import com.bod.bod.challenge.repository.ChallengeRepository;
import com.bod.bod.global.dto.PaginationResponse;
import com.bod.bod.global.service.S3Service;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import com.bod.bod.user.repository.UserRepository;
import com.bod.bod.verification.repository.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ChallengeRepository challengeRepository;

    @Mock
    VerificationRepository verificationRepository;

    @Mock
    S3Service s3Service;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    AdminService adminService;

    private User adminUser;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // 각 테스트마다 유저 및 관리자를 초기화
        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@example.com")
                .password("securePassword")
                .name("Admin User")
                .nickname("Admin")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.ADMIN)
                .build();

        user1 = User.builder()
                .id(2L)
                .username("user1")
                .email("user1@example.com")
                .password("password1")
                .name("User One")
                .nickname("User1")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.USER)
                .build();

        user2 = User.builder()
                .id(3L)
                .username("user2")
                .email("user2@example.com")
                .password("password2")
                .name("User Two")
                .nickname("User2")
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.USER)
                .build();
    }

    @Test
    @DisplayName("전체 유저 조회 - 관리자 권한")
    void getAllUsers_asAdmin() {
        // Given

        // Mocking the behavior of userRepository
        when(userRepository.findAllByOrderByCreatedAtAsc(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user1, user2)));

        int page = 0;
        int size = 10;

        // When
        PaginationResponse<AdminUsersResponseDto> result = adminService.getAllUsers(page, size, adminUser);

        // Then
        assertEquals(2, result.getContent().size()); // 유저 수 확인
        assertEquals(1, result.getTotalPages()); // 전체 페이지 수 확인
    }
}
