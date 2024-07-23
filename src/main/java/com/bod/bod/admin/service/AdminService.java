package com.bod.bod.admin.service;

import com.bod.bod.admin.dto.AdminUsersResponseDto;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public Page<AdminUsersResponseDto> getAllUsers(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction,sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userList;
        userList = userRepository.findAll(pageable);

        return userList.map(AdminUsersResponseDto::new);
    }
}
