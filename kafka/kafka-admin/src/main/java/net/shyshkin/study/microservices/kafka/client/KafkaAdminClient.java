package net.shyshkin.study.microservices.kafka.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAdminClient {

    private final KafkaConfigData kafkaConfigData;

    private final AdminClient adminClient;

    private final WebClient webClient;

    @Retryable(interceptor = "defaultRetryInterceptor")
    public void createTopics() {

//        if (true) throw new RuntimeException("Fake exception");

        List<NewTopic> newTopics = kafkaConfigData
                .getTopicNamesToCreate()
                .stream()
                .map(this::newTopic)
                .collect(Collectors.toList());
        var createTopicsResult = adminClient.createTopics(newTopics);
        try {
            createTopicsResult.all().get(1L, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            throw new RuntimeException("Exception while creating topics", e);
        }
    }

    @Retryable(interceptor = "defaultRetryInterceptor")
    public void checkSchemaRegistry() {
        if (!getSchemaRegistryStatus().is2xxSuccessful()) {
            throw new RuntimeException();
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return webClient
                    .get()
                    .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()))
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private NewTopic newTopic(String name) {
        return new NewTopic(
                name,
                kafkaConfigData.getNumberOfPartitions(),
                kafkaConfigData.getReplicationFactor());
    }

    @Retryable(interceptor = "defaultRetryInterceptor")
    public void checkTopicsCreated(Collection<TopicListing> topics) {
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            if (!isTopicCreated(topic, topics)) {
                throw new RuntimeException("Topic is not created yet");
            }
        }
    }

    private boolean isTopicCreated(String topicName, Collection<TopicListing> topics) {
        if (topics == null) return false;
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    @Retryable(interceptor = "defaultRetryInterceptor")
    public Collection<TopicListing> getTopics() {
        try {
            Collection<TopicListing> topicListings = adminClient.listTopics()
                    .listings()
                    .get();
            topicListings.forEach(topic -> log.debug("Topic with name {} is ready", topic.name()));
            return topicListings;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
