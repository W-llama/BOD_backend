package com.bod.bod.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUserCountResponseDto {

    private Long usersCount;

    private Long activeUsersCount;

    private Long withdrawUsersCount;

    private Long adminUsersCount;

    private Long userUsersCount;

    public AdminUserCountResponseDto(long usersCount, long activeUsersCount, long withdrawUsersCount, long adminUsersCount, long userUsersCount) {
        this.usersCount = usersCount;
        this.activeUsersCount = activeUsersCount;
        this.withdrawUsersCount = withdrawUsersCount;
        this.adminUsersCount = adminUsersCount;
        this.userUsersCount = userUsersCount;
    }

}
