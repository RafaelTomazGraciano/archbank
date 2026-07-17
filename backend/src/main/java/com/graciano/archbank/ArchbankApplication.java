package com.graciano.archbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ArchbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArchbankApplication.class, args);
	}

}
