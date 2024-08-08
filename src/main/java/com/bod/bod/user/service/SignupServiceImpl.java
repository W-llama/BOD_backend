package com.bod.bod.user.service;

import com.bod.bod.global.exception.ErrorCode;
import com.bod.bod.global.exception.GlobalException;
import com.bod.bod.global.jwt.security.RedisEmailAuthentication;
import com.bod.bod.user.dto.EmailAddressDto;
import com.bod.bod.user.dto.EmailDto;
import com.bod.bod.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

  private final UserRepository userRepository;

  private final MailServiceImpl mailService;
  private final RedisEmailAuthentication redisEmailAuthentication;

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

  @Override
  public EmailAddressDto sendAuthenticationCode(String email) throws MessagingException {
	String code = createRandomCode();
	redisEmailAuthentication.setEmailAuthenticationExpire(email, code, 5L);

	String text = "";
	text += "안녕하세요. Challengers-BOD입니다.";
	text += "<br/><br/>";
	text += "인증코드 보내드립니다.";
	text += "<br/><br/>";
	text += "인증코드 : <b>" + code + "</b>";

	EmailDto emailDto = new EmailDto(email, "Challengers-BOD 회원가입 이메일 인증코드 발송 메일입니다.", text);
	mailService.sendEmail(emailDto);
	return new EmailAddressDto(email);
  }

  @Override
  public void emailAuthentication(String email, String code) {
	String sendCode = redisEmailAuthentication.getEmailAuthenticationCode(email);

	if (sendCode == null) {
	  throw new GlobalException(ErrorCode.NOT_FOUND_EMAIL);
	}

	if (!sendCode.equals(code)) {
	  throw new GlobalException(ErrorCode.MISMATCH_VERIFICATION_CODE);
	}

	redisEmailAuthentication.setEmailAuthenticationComplete(email);
  }

  private String createRandomCode() {
	int number = (int) (Math.random() * (90000)) + 100000;
	return String.valueOf(number);
  }
}
