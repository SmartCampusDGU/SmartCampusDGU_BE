package org.smartcampus.smartcampus_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartcampusBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartcampusBeApplication.class, args);
	}

}
