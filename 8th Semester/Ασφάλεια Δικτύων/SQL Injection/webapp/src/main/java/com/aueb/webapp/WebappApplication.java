package com.aueb.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebappApplication {

	public static String loginMessage= "Hello there! There's nothing here yet";
	public static void main(String[] args) {
		SpringApplication.run(WebappApplication.class, args);
	}

	@GetMapping
	public String hello(){
		return loginMessage;
	}

}
