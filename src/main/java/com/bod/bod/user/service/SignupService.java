package com.bod.bod.user.service;

public interface SignupService {

	boolean checkUsernameExists(String username);

	boolean checkEmailExists(String email);

	boolean checkNicknameExists(String nickname);

	boolean checkAdminToken(String adminToken);
}
