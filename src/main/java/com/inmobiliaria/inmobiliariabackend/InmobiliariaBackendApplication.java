package com.inmobiliaria.inmobiliariabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InmobiliariaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(InmobiliariaBackendApplication.class, args);
    }

}
