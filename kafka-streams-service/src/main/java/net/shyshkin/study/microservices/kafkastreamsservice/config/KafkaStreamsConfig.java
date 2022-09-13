package net.shyshkin.study.microservices.kafkastreamsservice.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import net.shyshkin.study.microservices.config.KafkaStreamsConfigData;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaStreamsConfig {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaStreamsConfigData kafkaStreamsConfigData;

    @Bean
    @Qualifier("streamsConfiguration")
    Properties streamsConfiguration() {
        Properties properties = new Properties();
        properties.putAll(Map.of(
                StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamsConfigData.getApplicationId(),
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers(),
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfigData.getSchemaRegistry().getUrl(),
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
                StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
                StreamsConfig.STATE_DIR_CONFIG, kafkaStreamsConfigData.getStateFileLocation()
        ));
        return properties;
    }


}
