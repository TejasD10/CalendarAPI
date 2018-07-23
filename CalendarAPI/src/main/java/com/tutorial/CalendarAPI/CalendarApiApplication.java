package com.tutorial.CalendarAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.tutorial.CalendarAPI.repositories")
@SpringBootApplication
public class CalendarApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalendarApiApplication.class, args);
	}
}
