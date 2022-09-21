package net.shyshkin.study.microservices.analyticsservice.business.consumer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.analyticsservice.business.consumer.KafkaConsumer;
import net.shyshkin.study.microservices.analyticsservice.dataaccess.entity.AnalyticsEntity;
import net.shyshkin.study.microservices.analyticsservice.dataaccess.repository.AnalyticsRepository;
import net.shyshkin.study.microservices.analyticsservice.mapper.AnalyticsMapper;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAnalyticsAvroModel;
import net.shyshkin.study.microservices.kafka.client.KafkaAdminClient;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsKafkaConsumer implements KafkaConsumer<TwitterAnalyticsAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final AnalyticsRepository analyticsRepository;
    private final AnalyticsMapper mapper;

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

    @Override
    @KafkaListener(id = "twitterAnalyticsTopicListener", topics = "${kafka-config.topic-name}", autoStartup = "false")
    public void receive(
            @Payload List<TwitterAnalyticsAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());

        List<AnalyticsEntity> analyticsEntities = mapper.toEntityList(messages);
        analyticsRepository.batchPersist(analyticsEntities);
        log.debug("{} number of messages send to database", analyticsEntities.size());

    }

}

