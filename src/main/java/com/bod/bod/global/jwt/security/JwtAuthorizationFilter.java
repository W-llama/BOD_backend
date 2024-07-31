package com.bod.bod.global.jwt.security;

import com.bod.bod.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailServiceImpl userDetailsService;

	public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailServiceImpl userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
		String accessToken = jwtUtil.getTokenFromHeader(JwtUtil.AUTHORIZATION_HEADER, req);

		if (StringUtils.hasText(accessToken)) {
			try {
				Claims info = jwtUtil.getUserInfoFromToken(accessToken);
				setAuthentication(info.getSubject());
			} catch (ExpiredJwtException e) {
				handleExpiredAccessToken(req, res);
				return;
			} catch (Exception e) {
				log.error("Token Error: " + e.getMessage());
				SecurityContextHolder.clearContext();
				return;
			}
		}
		filterChain.doFilter(req, res);
	}

	private void handleExpiredAccessToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String refreshToken = jwtUtil.getTokenFromHeader(JwtUtil.REFRESH_HEADER, req);

		if (StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)) {
			String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
			jwtUtil.addJwtToHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + newAccessToken, res);
			Claims info = jwtUtil.getUserInfoFromToken(newAccessToken);
			setAuthentication(info.getSubject());
		} else {
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			res.setContentType("application/json");
			res.setCharacterEncoding("UTF-8");
			res.getWriter().write("{\"message\":\"리프레시 토큰으로 재발급 받으세요\"}");
		}
	}

	// 인증 처리
	private void setAuthentication(String username) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = createAuthentication(username);
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	// 인증 객체 생성
	private Authentication createAuthentication(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}
