package com.bod.bod.admin.dto;

import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.entity.UserStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AdminUsersResponseDto {

    private Long userId;

    private String name;

    private String email;

    private LocalDateTime createdAt;

    private UserStatus userStatus;

    private UserRole userRole;

    private Long point;


    public AdminUsersResponseDto(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.userStatus = user.getUserStatus();
        this.userRole = user.getUserRole();
        this.point = user.getPoint();

    }

}
