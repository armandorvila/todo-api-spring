package com.armandorv.poc.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.reactive.config.EnableWebFlux;

import com.armandorv.poc.tasks.config.ApplicationProperties;

@SpringBootApplication
@EnableWebFlux
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
