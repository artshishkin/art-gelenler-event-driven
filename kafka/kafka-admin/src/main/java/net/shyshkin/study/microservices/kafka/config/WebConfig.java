package net.shyshkin.study.microservices.kafka.config;

import net.shyshkin.study.microservices.config.KafkaConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    @Bean
    WebClient webClient(KafkaConfigData kafkaConfigData) {
        return WebClient.builder()
                .baseUrl(kafkaConfigData.getSchemaRegistry().getUrl())
                .build();
    }

}
