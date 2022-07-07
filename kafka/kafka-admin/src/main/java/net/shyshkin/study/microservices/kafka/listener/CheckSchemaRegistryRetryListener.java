package net.shyshkin.study.microservices.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.kafka.exception.KafkaClientException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckSchemaRegistryRetryListener extends RetryListenerSupport {

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.debug("Checking Schema Registry Status, attempt {}", context.getRetryCount());
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        throw new KafkaClientException("Reached max number of retry for Check Schema Registry!");
    }
}
