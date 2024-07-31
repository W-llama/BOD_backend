package com.bod.bod.global.jwt.security.OAuth2;

import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.user.entity.UserRole;
import com.bod.bod.user.oauth2.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication)
		throws IOException, ServletException
	{
		CustomOAuth2User oAuth2User =(CustomOAuth2User)authentication.getPrincipal();
		String accessToken = jwtUtil.createAccessToken(oAuth2User.getUsername(), UserRole.USER);
		String refreshToken = jwtUtil.createRefreshToken(oAuth2User.getUsername());

		jwtUtil.addJwtToHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + accessToken, response);
		jwtUtil.addRefreshTokenCookie(response, refreshToken);

		String targetUrl = "http://localhost:8081?token=" + JwtUtil.BEARER_PREFIX + accessToken;
		getRedirectStrategy().sendRedirect(request, response, targetUrl);

	}
}
