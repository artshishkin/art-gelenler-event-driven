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
public class CreateTopicsRetryListener extends RetryListenerSupport {

    private final KafkaConfigData kafkaConfigData;

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        List<String> topicNamesToCreate = kafkaConfigData.getTopicNamesToCreate();
        log.debug("Creating {} topics, attempt {}", topicNamesToCreate.size(), context.getRetryCount());
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {

        Object exhausted = context.getAttribute(RetryContext.EXHAUSTED);
        if (exhausted != null && ((boolean) exhausted))
            throw new KafkaClientException("Reached max number of retry for creating Kafka topic(s)!");
    }
}
