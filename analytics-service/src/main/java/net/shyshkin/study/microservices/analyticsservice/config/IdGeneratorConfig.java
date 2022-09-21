package net.shyshkin.study.microservices.analyticsservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

@Configuration
public class IdGeneratorConfig {

    @Bean
    IdGenerator idGenerator() {
        return new JdkIdGenerator();
    }
}
