package com.bod.bod.global.jwt.security;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.user.entity.User;
import com.bod.bod.user.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_USERNAME));

		return new UserDetailsImpl(user);
	}
}
