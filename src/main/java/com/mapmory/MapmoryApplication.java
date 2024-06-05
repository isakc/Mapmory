package com.mapmory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MapmoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MapmoryApplication.class, args);
	}

}
