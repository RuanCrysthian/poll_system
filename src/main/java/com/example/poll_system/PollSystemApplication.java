package com.example.poll_system;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class PollSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(PollSystemApplication.class, args);
	}

}
