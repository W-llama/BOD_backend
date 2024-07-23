package com.bod.bod.admin.dto;

import com.bod.bod.user.entity.User;
import lombok.Getter;

@Getter
public class AdminUsersResponseDto {

    private String email;

    private String name;

    public AdminUsersResponseDto(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
    }

}
