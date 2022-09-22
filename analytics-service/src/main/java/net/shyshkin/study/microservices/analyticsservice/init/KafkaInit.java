package net.shyshkin.study.microservices.analyticsservice.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.kafka.client.KafkaAdminClient;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaInit {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        Collection<TopicListing> topics = kafkaAdminClient.getTopics();
        kafkaAdminClient.checkTopicsCreated(topics);
        List<String> topicNames = topics.stream()
                .map(TopicListing::name)
                .collect(Collectors.toList());
        log.debug("Topics with name `{}` are ready for operations", topicNames);
        Objects.requireNonNull(
                        kafkaListenerEndpointRegistry.getListenerContainer("twitterAnalyticsTopicListener")
                )
                .start();
    }
}
