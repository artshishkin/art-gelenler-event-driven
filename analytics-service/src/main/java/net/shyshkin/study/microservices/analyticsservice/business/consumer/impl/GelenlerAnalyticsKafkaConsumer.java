package net.shyshkin.study.microservices.analyticsservice.business.consumer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.analyticsservice.business.consumer.KafkaConsumer;
import net.shyshkin.study.microservices.analyticsservice.dataaccess.entity.AnalyticsEntity;
import net.shyshkin.study.microservices.analyticsservice.dataaccess.repository.AnalyticsRepository;
import net.shyshkin.study.microservices.analyticsservice.mapper.AnalyticsMapper;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAnalyticsAvroModel;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("gelenler")
public class GelenlerAnalyticsKafkaConsumer implements KafkaConsumer<TwitterAnalyticsAvroModel> {

    private final AnalyticsRepository analyticsRepository;
    private final AnalyticsMapper mapper;

    @Override
    @KafkaListener(id = "twitterAnalyticsTopicListener", topics = "${kafka-config.topic-name}", autoStartup = "false")
    public void receive(
            @Payload List<TwitterAnalyticsAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "persisting them: Thread id {}",
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

