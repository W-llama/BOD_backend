package com.bod.bod.admin.dto;

import com.bod.bod.user.entity.User;
import lombok.Getter;

@Getter
public class AdminUserUpdateResponseDto {

    private Long point;

    public AdminUserUpdateResponseDto(User user) {
        this.point = user.getPoint();
    }

}
