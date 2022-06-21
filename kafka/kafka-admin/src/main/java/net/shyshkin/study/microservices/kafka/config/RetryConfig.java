package net.shyshkin.study.microservices.kafka.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.config.RetryConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@RequiredArgsConstructor
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate(RetryConfigData retryConfigData) {
        return RetryTemplate.builder()
                .maxAttempts(retryConfigData.getMaxAttempts())
                .exponentialBackoff(retryConfigData.getInitialIntervalMs(), retryConfigData.getMultiplier(), retryConfigData.getMaxIntervalMs())
                .build();
    }

}
