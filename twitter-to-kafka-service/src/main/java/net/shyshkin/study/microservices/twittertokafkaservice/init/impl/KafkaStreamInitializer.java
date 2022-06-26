package net.shyshkin.study.microservices.twittertokafkaservice.init.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import net.shyshkin.study.microservices.kafka.client.KafkaAdminClient;
import net.shyshkin.study.microservices.twittertokafkaservice.init.StreamInitializer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamInitializer implements StreamInitializer {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaAdminClient kafkaAdminClient;

    @Override
    public void init() {

        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemaRegistry();
        log.debug("Topics `{}` are ready for operations!", kafkaConfigData.getTopicNamesToCreate());

    }

}
