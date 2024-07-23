package com.bod.bod.admin.controller;

import com.bod.bod.admin.dto.AdminUsersResponseDto;
import com.bod.bod.admin.service.AdminService;
import com.bod.bod.global.dto.CommonResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        @RequestParam(value = "sortBy",defaultValue = "createdAt") String sortBy,
        @RequestParam(value = "isAsc", defaultValue = "true") boolean isAsc
    ) {
        Page<AdminUsersResponseDto> responseDto = adminService.getAllUsers(page-1, size, sortBy, isAsc);
        List<AdminUsersResponseDto> usersList = responseDto.getContent();
        return ResponseEntity.ok().body(new CommonResponseDto<>
            (HttpStatus.OK.value(), "전체 회원 조회에 성공하였습니다!", usersList));
    }
}
