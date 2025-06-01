package com.example.poll_system.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.poll_system.infrastructure.persistence.jpa.repositories")
@EntityScan(basePackages = "com.example.poll_system.infrastructure.persistence.jpa.entities")
public class JpaConfig {
}
