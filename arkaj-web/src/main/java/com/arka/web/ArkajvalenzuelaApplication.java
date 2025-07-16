package com.arka.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.arka"})
@EntityScan(basePackages = "com.arka.infrastructure.adapter.out.persistence.entity")
public class ArkajvalenzuelaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArkajvalenzuelaApplication.class, args);
	}

}
