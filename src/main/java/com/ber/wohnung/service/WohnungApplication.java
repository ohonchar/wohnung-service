package com.ber.wohnung.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class WohnungApplication {

	public static void main(String[] args) {
		SpringApplication.run(WohnungApplication.class, args);
	}

}
