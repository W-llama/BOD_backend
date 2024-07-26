package com.bod.bod.user.service;

import com.bod.bod.user.dto.EditPasswordRequestDto;
import com.bod.bod.user.dto.LoginRequestDto;
import com.bod.bod.user.dto.EditProfileRequestDto;
import com.bod.bod.user.dto.SignUpRequestDto;
import com.bod.bod.user.dto.UserResponseDto;
import com.bod.bod.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

	@Transactional
	void signUp(SignUpRequestDto signUpRequestDto);

	@Transactional
	void login(LoginRequestDto loginRequestDto, HttpServletResponse response);

	@Transactional
	void logout(HttpServletRequest request, HttpServletResponse response, User user);

	@Transactional
	void withdraw(LoginRequestDto loginRequestDto, User user, HttpServletResponse response);

	UserResponseDto getMyProfile(User user);

	UserResponseDto getUserprofile(long userId);

	@Transactional
	UserResponseDto editProfile(EditProfileRequestDto editProfileRequestDto, User user);

	@Transactional
	UserResponseDto editProfileImage(MultipartFile profileImage, User user);

	@Transactional
	UserResponseDto editPassword(EditPasswordRequestDto editPasswordRequestDto, User user);

	User findById(long userId);
}
