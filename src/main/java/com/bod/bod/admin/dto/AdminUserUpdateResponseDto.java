package com.bod.bod.admin.dto;

import com.bod.bod.user.entity.User;
import lombok.Getter;

@Getter
public class AdminUserUpdateResponseDto {

    private String name;

    public AdminUserUpdateResponseDto(User user) {
        this.name = user.getName();
    }

}
