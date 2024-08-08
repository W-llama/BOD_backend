package com.bod.bod.user.service;

import com.bod.bod.user.dto.EmailDto;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.smtp.address}")
  private String address;

  @Autowired
  public MailServiceImpl(JavaMailSender mailSender) {
	this.mailSender = mailSender;
  }

  @Override
  public String sendEmail(EmailDto dto) throws MessagingException {
	MimeMessage message = createMessage(dto.getEmail(), dto.getTitle(), dto.getText());
	mailSender.send(message);
	return dto.getEmail();
  }

  private MimeMessage createMessage(String recipient, String title, String text) throws MessagingException {
	MimeMessage message = mailSender.createMimeMessage();
	message.setFrom(address);
	message.setRecipients(Message.RecipientType.TO, recipient);
	message.setSubject(title);
	message.setText(text, "UTF-8", "html");
	return message;
  }
}
