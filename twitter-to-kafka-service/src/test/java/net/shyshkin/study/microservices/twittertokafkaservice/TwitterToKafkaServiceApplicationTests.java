package net.shyshkin.study.microservices.twittertokafkaservice;

import net.shyshkin.study.microservices.twittertokafkaservice.config.TwitterToKafkaServiceConfigData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TwitterToKafkaServiceApplicationTests {

    @Autowired
    private TwitterToKafkaServiceConfigData configData;

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
