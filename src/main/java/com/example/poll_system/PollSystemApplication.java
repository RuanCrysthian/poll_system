package com.example.poll_system;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRabbit
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class PollSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PollSystemApplication.class, args);
	}

}
