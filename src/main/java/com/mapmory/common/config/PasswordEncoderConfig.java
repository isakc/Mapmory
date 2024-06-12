package com.mapmory.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

	@Bean
	public static PasswordEncoder generatePasswordEncoder() {
		
		final int saltLength = 16;
		final int hashLength = 32;
		final int parallelism = Runtime.getRuntime().availableProcessors();
		final int memory = 65536;
		final int iterations = 2;
		
		return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
	}
}