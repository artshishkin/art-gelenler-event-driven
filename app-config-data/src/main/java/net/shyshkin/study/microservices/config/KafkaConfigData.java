package net.shyshkin.study.microservices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {

    private String bootstrapServers;
    private SchemaRegistry schemaRegistry;
    private String topicName;
    private List<String> topicNamesToCreate;
    private Integer numberOfPartitions;
    private Short replicationFactor;

    @Data
    public static class SchemaRegistry {
        private String key;
        private String url;
    }

}
