package com.databinder.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.databinder.core", "com.databinder.scrapping", 
											"com.databinder.config", "com.databinder.search",
											"com.databinder.scheduler", "com.databinder.alert"})
@EnableJpaRepositories(basePackages = {"com.databinder.core.repositories", "com.databinder.config.repositories"})
@EntityScan(basePackages = {"com.databinder.core.entities", "com.databinder.config.entities"})
@EnableScheduling
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}
