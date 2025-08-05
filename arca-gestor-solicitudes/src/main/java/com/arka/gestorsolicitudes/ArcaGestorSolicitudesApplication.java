package com.arka.gestorsolicitudes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ArcaGestorSolicitudesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArcaGestorSolicitudesApplication.class, args);
    }
}
