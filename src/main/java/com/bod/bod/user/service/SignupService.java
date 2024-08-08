package com.bod.bod.user.service;

import com.bod.bod.user.dto.EmailAddressDto;
import jakarta.mail.MessagingException;
import org.springframework.transaction.annotation.Transactional;

public interface SignupService {

  boolean checkUsernameExists(String username);

  boolean checkEmailExists(String email);

  boolean checkNicknameExists(String nickname);

  boolean checkAdminToken(String adminToken);

  @Transactional
  EmailAddressDto sendAuthenticationCode(String email) throws MessagingException;

  @Transactional
  void emailAuthentication(String email, String code);
}
