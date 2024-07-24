package com.bod.bod.admin.dto;

import com.bod.bod.user.entity.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUserStatusUpdateRequestDto {

    private UserStatus userStatus;

}
