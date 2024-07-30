package com.bod.bod.user.service;

import com.bod.bod.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService{

	private final UserRepository userRepository;

	@Value("${jwt.secret.key}")
	private String secretKey;


	@Override
	public boolean checkUsernameExists(String username) {
		return userRepository.findByUsername(username).isPresent();
	}

	@Override
	public boolean checkEmailExists(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	@Override
	public boolean checkNicknameExists(String nickname) {
		return userRepository.findByNickname(nickname).isPresent();
	}

	@Override
	public boolean checkAdminToken(String adminToken) {
		return adminToken.equals(secretKey);
	}
}
