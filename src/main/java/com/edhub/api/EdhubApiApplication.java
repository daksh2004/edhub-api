package com.edhub.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EdhubApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdhubApiApplication.class, args);
    }

}

