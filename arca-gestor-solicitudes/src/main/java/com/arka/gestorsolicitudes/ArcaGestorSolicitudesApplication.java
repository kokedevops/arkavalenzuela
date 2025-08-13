package com.arka.gestorsolicitudes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
    "com.arka.gestorsolicitudes",
    "com.arka.security"
})
public class ArcaGestorSolicitudesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArcaGestorSolicitudesApplication.class, args);
    }
}
