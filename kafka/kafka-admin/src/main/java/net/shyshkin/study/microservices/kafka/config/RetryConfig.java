package net.shyshkin.study.microservices.kafka.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@RequiredArgsConstructor
@EnableRetry
public class RetryConfig {

//    @Bean
//    public RetryTemplate retryTemplate(RetryConfigData retryConfigData) {
//        return RetryTemplate.builder()
//                .maxAttempts(retryConfigData.getMaxAttempts())
//                .exponentialBackoff(retryConfigData.getInitialIntervalMs(), retryConfigData.getMultiplier(), retryConfigData.getMaxIntervalMs())
//                .build();
//    }

}
