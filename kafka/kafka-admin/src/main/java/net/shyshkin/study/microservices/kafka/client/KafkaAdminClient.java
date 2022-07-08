package net.shyshkin.study.microservices.kafka.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAdminClient {

    private final KafkaConfigData kafkaConfigData;

    private final AdminClient adminClient;

    private final WebClient webClient;

//    @Autowired
//    private KafkaAdminClient self;

    @Retryable(
            maxAttemptsExpression = "#{@retryConfigData.getMaxAttempts()}",
            backoff = @Backoff(
                    delayExpression = "#{@retryConfigData.getInitialIntervalMs()}",
                    multiplierExpression = "#{@retryConfigData.getMultiplier()}"
            ),
            listeners = {"createTopicsRetryListener"}
    )
    public void createTopics() {

        CreateTopicsResult createTopicsResult;

        createTopicsResult = this.doCreateTopics();
    }

    @Retryable(
            maxAttemptsExpression = "#{@retryConfigData.getMaxAttempts()}",
            backoff = @Backoff(
                    delayExpression = "#{@retryConfigData.getInitialIntervalMs()}",
                    multiplierExpression = "#{@retryConfigData.getMultiplier()}"
            ),
            listeners = {"checkSchemaRegistryRetryListener"}
    )
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

    private CreateTopicsResult doCreateTopics() {
        List<String> topicNamesToCreate = kafkaConfigData.getTopicNamesToCreate();
        List<NewTopic> newTopics = topicNamesToCreate
                .stream()
                .map(this::newTopic)
                .collect(Collectors.toList());
        return adminClient.createTopics(newTopics);
    }

    private NewTopic newTopic(String name) {
        return new NewTopic(
                name,
                kafkaConfigData.getNumberOfPartitions(),
                kafkaConfigData.getReplicationFactor());
    }

    @Retryable(
            maxAttemptsExpression = "#{@retryConfigData.getMaxAttempts()}",
            backoff = @Backoff(
                    delayExpression = "#{@retryConfigData.getInitialIntervalMs()}",
                    multiplierExpression = "#{@retryConfigData.getMultiplier()}"
            ),
            listeners = {"checkTopicsCreatedRetryListener"}
    )
    public void checkTopicsCreated(Collection<TopicListing> topics) {

//        if (true) throw new RuntimeException("Fake exception");

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

    @Retryable(
            maxAttemptsExpression = "#{@retryConfigData.getMaxAttempts()}",
            backoff = @Backoff(
                    delayExpression = "#{@retryConfigData.getInitialIntervalMs()}",
                    multiplierExpression = "#{@retryConfigData.getMultiplier()}"
            ),
            listeners = {"getTopicsRetryListener"}
    )
    public Collection<TopicListing> getTopics() {
        Collection<TopicListing> topicListings;
        topicListings = this.doGetTopics();
        return topicListings;
    }

    private Collection<TopicListing> doGetTopics() {
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
