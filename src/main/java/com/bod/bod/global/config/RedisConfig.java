package com.bod.bod.global.config;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.password}")
  private String password;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
	final RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
	redisStandaloneConfiguration.setHostName(host);
	redisStandaloneConfiguration.setPort(port);
	redisStandaloneConfiguration.setPassword((password));
	return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate() {
	RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
	redisTemplate.setKeySerializer(new StringRedisSerializer());
	redisTemplate.setValueSerializer(new StringRedisSerializer());
	redisTemplate.setConnectionFactory(redisConnectionFactory());
	return redisTemplate;
  }

  @Bean
  public RedisTemplate<String, Map<String, String>> redisEmailAuthenticationTemplate() {
	RedisTemplate<String, Map<String, String>> redisTemplate = new RedisTemplate<>();
	redisTemplate.setConnectionFactory(redisConnectionFactory());
	redisTemplate.setHashKeySerializer(new StringRedisSerializer());
	redisTemplate.setHashValueSerializer(new StringRedisSerializer());
	return redisTemplate;
  }
}