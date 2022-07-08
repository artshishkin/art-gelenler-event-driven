package net.shyshkin.study.microservices.config;

import lombok.Data;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfigData {

    private Class<? extends Serializer<?>> keySerializerClass;
    private Class<? extends Serializer<?>> valueSerializerClass;
    private CompressionType compressionType;
    private String acks;
    private Integer batchSize;
    private Integer batchSizeBoostFactor;
    private Integer lingerMs;
    private Integer requestTimeoutMs;
    private Integer retryCount;

}
