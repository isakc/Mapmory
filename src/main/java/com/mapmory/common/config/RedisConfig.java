package com.mapmory.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	
	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private int port;
	
	@Value("${spring.redis.password}")
	private String password;
	
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		
		RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(password);
		
		// return new LettuceConnectionFactory(host, port);
		return new LettuceConnectionFactory(redisConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		
		redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		
		return redisTemplate;
	}
}