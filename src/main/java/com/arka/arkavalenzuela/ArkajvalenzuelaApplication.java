package com.arka.arkavalenzuela;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 🔔 Enable scheduling for abandoned cart notifications
public class ArkajvalenzuelaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArkajvalenzuelaApplication.class, args);
	}

}
