package net.shyshkin.study.microservices.twittertokafkaservice;

import net.shyshkin.study.microservices.config.TwitterToKafkaServiceConfigData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import twitter4j.TwitterStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "twitter-to-kafka-service.mock.enable=false"
})
class TwitterToKafkaServiceApplicationTests {

    @Autowired
    private TwitterToKafkaServiceConfigData configData;

    @MockBean
    TwitterStream twitterStream;

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
