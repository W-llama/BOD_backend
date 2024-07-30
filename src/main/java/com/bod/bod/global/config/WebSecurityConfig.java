package com.bod.bod.global.config;

import com.bod.bod.global.jwt.JwtUtil;
import com.bod.bod.global.jwt.security.JwtAuthorizationFilter;
import com.bod.bod.global.jwt.security.UserDetailServiceImpl;
import com.bod.bod.user.service.CustomOAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final UserDetailServiceImpl userDetailsService;
	private final JwtUtil jwtUtil;
	private final CustomOAuth2UserServiceImpl customOAuth2UserService;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthorizationFilter jwtAuthenticationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화

			.formLogin(form -> form.disable())  // 기본 폼 로그인 방식 비활성화

			.httpBasic(basic -> basic.disable())  // HTTP Basic 인증 방식 비활성화

			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 설정 : STATELESS

			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.POST, "/api/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/challenges/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/admins/**").hasRole("ADMIN")
				.anyRequest().authenticated())
			.oauth2Login(oauth2 -> oauth2.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserService)))  // OAuth2 로그인 설정
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

		return http.build();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
					.allowedOrigins("http://localhost:8081")
					.exposedHeaders("authorization") // 이 부분을 추가합니다.
					.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/**")
					.addResourceLocations("classpath:/META-INF/resources/");
				registry.addResourceHandler("index.html")
					.addResourceLocations("classpath:/META-INF/resources/")
					.setCacheControl(CacheControl.noStore()) // 브라우저 resource 저장 사용 x
					.setCachePeriod(0); // 서버 캐시 사용하지 않음.
			}
		};
	}
}
