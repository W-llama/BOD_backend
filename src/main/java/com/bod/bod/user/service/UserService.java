package com.bod.bod.user.service;

import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

	@Transactional
	UserResponseDto signUp(SignUpRequestDto signUpRequestDto);

	@Transactional
	UserResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response);

	@Transactional
	void logout(HttpServletRequest request, HttpServletResponse response);
}
