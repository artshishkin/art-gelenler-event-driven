package net.shyshkin.study.microservices.kafka.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import net.shyshkin.study.microservices.config.RetryConfigData;
import net.shyshkin.study.microservices.kafka.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAdminClient {

    private final KafkaConfigData kafkaConfigData;

    private final RetryConfigData retryConfigData;

    private final AdminClient adminClient;

    private final RetryTemplate retryTemplate;

    public void createTopic() {

        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::doCreateTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max number of retry for creating Kafka topic(s)!");
        }
        checkTopicsCreated();
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicNamesToCreate = kafkaConfigData.getTopicNamesToCreate();
        log.debug("Creating {} topics, attempt {}", topicNamesToCreate.size(), retryContext.getRetryCount());
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

    private void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        double multiplier = retryConfigData.getMultiplier();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topic, topics)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs = Math.round(sleepTimeMs * multiplier);
            }
        }
    }

    private boolean isTopicCreated(String topicName, Collection<TopicListing> topics) {
        if (topics == null) return false;
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private void checkMaxRetry(int i, Integer maxRetry) {
        if (i > maxRetry) {
            throw new KafkaClientException("Reached max number of retry for reading Kafka topic(s)!");
        }
    }

    private void sleep(long timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping for waiting new created topic(s)!");
        }
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topicListings;
        try {
            topicListings = retryTemplate.execute(this::doGetTopics);
        } catch (Exception e) {
            throw new KafkaClientException("Reached max number of retry for getting Kafka topic(s)!", e);
        }
        return topicListings;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        log.debug("Reading Kafka topics, attempt {}", retryContext.getRetryCount());
        Collection<TopicListing> topicListings = adminClient.listTopics()
                .listings()
                .get();
        topicListings.forEach(topic -> log.debug("Topic with name {} is ready", topic.name()));
        return topicListings;
    }

}
