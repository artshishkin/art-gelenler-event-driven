package net.shyshkin.study.microservices.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import net.shyshkin.study.microservices.kafka.exception.KafkaClientException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckTopicsCreatedRetryListener extends RetryListenerSupport {

    private final KafkaConfigData kafkaConfigData;

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        List<String> topicNamesToCreate = kafkaConfigData.getTopicNamesToCreate();
        log.debug("Checking {} topics to create, attempt {}", topicNamesToCreate.size(), context.getRetryCount());
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        throw new KafkaClientException("Reached max number of retry for waiting topics to be created!");
    }
}
