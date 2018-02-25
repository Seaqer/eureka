package ru.netflix.registryreplica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaRegistryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaRegistryServiceApplication.class, args);
    }
}
