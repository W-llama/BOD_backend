package com.bod.bod.global.jwt.security;

import com.bod.bod.user.entity.User;
import com.bod.bod.user.entity.UserRole;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

	private final User user;

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		UserRole role = user.getUserRole();
		String roleString = role.getAuthority();

		SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleString);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(simpleGrantedAuthority);

		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 계정이 만료되지 않았음을 표시
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // 계정이 잠기지 않았음을 표시
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 자격 증명이 만료되지 않았음을 표시
	}

	@Override
	public boolean isEnabled() {
		return true; // 계정이 활성화 되었음을 표시
	}
}
