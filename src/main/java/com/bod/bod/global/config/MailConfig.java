package com.bod.bod.global.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

  @Value("${spring.mail.host}")
  private String host;
  @Value("${spring.mail.username}")
  private String username;
  @Value("${spring.mail.password}")
  private String password;
  @Value("${spring.mail.smtp.port}")
  private int port;

  @Bean
  public JavaMailSender javaMailService() {
	JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
	javaMailSender.setHost(host);
	javaMailSender.setUsername(username);
	javaMailSender.setPassword(password);
	javaMailSender.setPort(port);
	javaMailSender.setJavaMailProperties(getMailProperties());
	javaMailSender.setDefaultEncoding("UTF-8");
	return javaMailSender;
  }

  private Properties getMailProperties() {
	Properties properties = new Properties();
	properties.setProperty("mail.smtp.starttls.enable", "true");
	properties.setProperty("mail.smtp.starttls.required", "true");
	properties.setProperty("mail.smtp.auth", "true");
	properties.setProperty("mail.smtp.ssl.trust", host);
	properties.setProperty("mail.debug", "true");
	return properties;
  }
}
