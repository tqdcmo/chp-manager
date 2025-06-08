package com.powerchp.chpmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication(scanBasePackages = "com.powerchp.chpmanager")
@EntityScan("com.powerchp.chpmanager.model")
public class ChpmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChpmanagerApplication.class, args);
	}
}

