package net.shyshkin.study.microservices.twittertokafkaservicev2;

import com.twitter.clientlib.api.TweetsApi;
import net.shyshkin.study.microservices.twittertokafkaservicev2.config.TwitterToKafkaServiceConfigData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TwitterToKafkaServiceV2ApplicationTests {

    @Autowired
    private TwitterToKafkaServiceConfigData configData;

    @MockBean
    TweetsApi tweetsApi;

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