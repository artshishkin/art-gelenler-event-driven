package net.shyshkin.study.microservices.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DelegatingRetryListener extends RetryListenerSupport {

    private final CreateTopicsRetryListener createTopicsRetryListener;
    private final CheckTopicsCreatedRetryListener checkTopicsCreatedRetryListener;
    private final GetTopicsRetryListener getTopicsRetryListener;
    private final CheckSchemaRegistryRetryListener checkSchemaRegistryRetryListener;

    private Map<String, RetryListener> listeners;

    @PostConstruct
    void init(){
        listeners = Map.of(
                "public void net.shyshkin.study.microservices.kafka.client.KafkaAdminClient.createTopics()", createTopicsRetryListener,
                "public void net.shyshkin.study.microservices.kafka.client.KafkaAdminClient.checkTopicsCreated(java.util.Collection<org.apache.kafka.clients.admin.TopicListing>)", checkTopicsCreatedRetryListener,
                "public java.util.Collection<org.apache.kafka.clients.admin.TopicListing> net.shyshkin.study.microservices.kafka.client.KafkaAdminClient.getTopics()", getTopicsRetryListener,
                "public void net.shyshkin.study.microservices.kafka.client.KafkaAdminClient.checkSchemaRegistry()", checkSchemaRegistryRetryListener
        );
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        RetryListener retryListener = listeners.get(context.getAttribute(RetryContext.NAME));
        retryListener.onError(context, callback, throwable);
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        RetryListener retryListener = listeners.get(context.getAttribute(RetryContext.NAME));
        retryListener.close(context, callback, throwable);
    }
}
