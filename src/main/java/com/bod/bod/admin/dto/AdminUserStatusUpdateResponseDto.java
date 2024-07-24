package com.bod.bod.admin.dto;

import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserStatus;
import lombok.Getter;

@Getter
public class AdminUserStatusUpdateResponseDto {

    private UserStatus userStatus;

    public AdminUserStatusUpdateResponseDto(User user) {
        this.userStatus = user.getUserStatus();
    }

}
