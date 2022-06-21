package net.shyshkin.study.microservices.kafka.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {

    @Bean
    public AdminClient adminClient(KafkaConfigData kafkaConfigData) {
        return AdminClient.create(Map.of(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers()
        ));
    }

}
