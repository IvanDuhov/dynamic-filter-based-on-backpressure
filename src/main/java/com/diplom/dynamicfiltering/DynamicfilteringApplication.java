package com.diplom.dynamicfiltering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DynamicfilteringApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamicfilteringApplication.class, args);
	}

}
