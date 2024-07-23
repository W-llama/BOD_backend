package com.bod.bod.global.jwt;

import com.bod.bod.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String REFRESH_HEADER = "Refresh";
	public static final String AUTHORIZATION_KEY = "auth";
	public static final String BEARER_PREFIX = "Bearer ";
	private static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

	@Value("${jwt.secret.key}")
	private String secretKey;

	@Value("${jwt.access-expire-time}")
	private long accessTokenExpireTime;

	@Value("${jwt.refresh-expire-time}")
	private long refreshTokenExpireTime;

	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String createAccessToken(String userName, UserRole userRole) {
		return createToken(userName, userRole, accessTokenExpireTime);
	}

	public String createRefreshToken(String userName) {
		return createToken(userName, null, refreshTokenExpireTime);
	}

	private String createToken(String userName, UserRole userRole, long expireTime) {
		Date now = new Date();
		JwtBuilder builder = Jwts.builder()
			.setSubject(userName)
			.setExpiration(new Date(now.getTime() + expireTime))
			.setIssuedAt(now)
			.signWith(key, signatureAlgorithm);

		if (userRole != null) {
			builder.claim(AUTHORIZATION_KEY, userRole);
		}

		return builder.compact();
	}

	public void addJwtToHeader(String headerName, String token, HttpServletResponse response) {
		response.addHeader(headerName, token);
	}

	public String getTokenFromHeader(String headerName, HttpServletRequest request) {
		String token = request.getHeader(headerName);
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(BEARER_PREFIX.length());
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			logger.error("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	public Claims getUserInfoFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		Cookie refreshTokenCookie = new Cookie(REFRESH_HEADER, refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge((int) refreshTokenExpireTime);
		response.addCookie(refreshTokenCookie);
	}

}
