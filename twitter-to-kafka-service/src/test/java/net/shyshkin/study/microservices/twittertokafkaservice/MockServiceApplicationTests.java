package net.shyshkin.study.microservices.twittertokafkaservice;

import net.shyshkin.study.microservices.config.TwitterToKafkaServiceConfigData;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAvroModel;
import net.shyshkin.study.microservices.kafka.client.KafkaAdminClient;
import net.shyshkin.study.microservices.kafka.producer.service.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "twitter-to-kafka-service.mock.enable=true",
        "twitter-to-kafka-service.mock.sleep-ms=10"
})
class MockServiceApplicationTests {

    @Autowired
    private TwitterToKafkaServiceConfigData configData;

    @MockBean
    KafkaAdminClient kafkaAdminClient;

    @MockBean
    KafkaProducer<Long, TwitterAvroModel> producer;

    @Test
    void contextLoads() {
        assertThat(configData)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("welcomeMessage", "Welcome to twitter-to-kafka-service")
                .satisfies(data -> assertThat(data.getTwitterKeywords())
                        .hasSize(5)
                        .allSatisfy(keyWord -> assertThat(keyWord).isNotEmpty()));
    }

}
