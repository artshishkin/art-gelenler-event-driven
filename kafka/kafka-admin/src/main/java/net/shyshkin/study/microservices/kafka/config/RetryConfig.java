package net.shyshkin.study.microservices.kafka.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.config.RetryConfigData;
import net.shyshkin.study.microservices.kafka.listener.DelegatingRetryListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@RequiredArgsConstructor
@EnableRetry
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate(
            RetryConfigData retryConfigData,
            DelegatingRetryListener listener) {
        return RetryTemplate.builder()
                .maxAttempts(retryConfigData.getMaxAttempts())
                .exponentialBackoff(retryConfigData.getInitialIntervalMs(), retryConfigData.getMultiplier(), retryConfigData.getMaxIntervalMs())
                .withListener(listener)
                .build();
    }

    @Bean
    RetryOperationsInterceptor defaultRetryInterceptor(RetryTemplate retryTemplate) {
        return RetryInterceptorBuilder
                .stateless()
                .retryOperations(retryTemplate)
                .build();
    }

//    @Bean
//    MethodInterceptor defaultRetryInterceptor(RetryTemplate retryTemplate) {
//        return invocation -> {
//            return retryTemplate.execute(context -> {
//                if (context.getRetryCount() > 0) {
//                    Object[] args = invocation.getArguments();
//                    args[0] = ((Integer) args[0]) + 1;
//                    args[1] = ((String) args[1]) + ((String) args[1]);
//                }
//                return invocation.proceed();
//            });
//        };
//    }

}
