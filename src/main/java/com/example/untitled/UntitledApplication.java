package com.example.untitled;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class UntitledApplication {

	@PostConstruct
	public void init() {
		// Set default timezone to UTC for the entire application
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
        SpringApplication.run(UntitledApplication.class, args);
	}

}
