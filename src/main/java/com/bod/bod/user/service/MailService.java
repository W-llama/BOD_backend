package com.bod.bod.user.service;

import com.bod.bod.user.dto.EmailDto;
import jakarta.mail.MessagingException;

public interface MailService {

  String sendEmail(EmailDto dto) throws MessagingException;
}
