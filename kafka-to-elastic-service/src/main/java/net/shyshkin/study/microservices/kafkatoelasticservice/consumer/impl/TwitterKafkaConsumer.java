package net.shyshkin.study.microservices.kafkatoelasticservice.consumer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import net.shyshkin.study.microservices.config.KafkaConsumerConfigData;
import net.shyshkin.study.microservices.elastic.index.client.service.ElasticIndexClient;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAvroModel;
import net.shyshkin.study.microservices.kafka.client.KafkaAdminClient;
import net.shyshkin.study.microservices.kafkatoelasticservice.consumer.KafkaConsumer;
import net.shyshkin.study.microservices.kafkatoelasticservice.mapper.ElasticModelMapper;
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
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final ElasticModelMapper elasticModelMapper;
    private final ElasticIndexClient<TwitterIndexModel> elasticIndexClient;
    private final KafkaConsumerConfigData kafkaConsumerConfigData;

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        Collection<TopicListing> topics = kafkaAdminClient.getTopics();
        kafkaAdminClient.checkTopicsCreated(topics);
        List<String> topicNames = topics.stream()
                .map(TopicListing::name)
                .collect(Collectors.toList());
        log.debug("Topics with name `{}` are ready for operations", topicNames);
        Objects.requireNonNull(
                        kafkaListenerEndpointRegistry.getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId())
                )
                .start();
    }

    @Override
    @KafkaListener(id = "#{kafkaConsumerConfigData.consumerGroupId}", topics = "${kafka-config.topic-name}")
    public void receive(
            @Payload List<TwitterAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());

        List<TwitterIndexModel> documents = elasticModelMapper.toElasticModels(messages);
        List<String> documentIds = elasticIndexClient.save(documents);
        log.debug("Documents saved to elasticsearch with ids {}", documentIds);

    }

}

