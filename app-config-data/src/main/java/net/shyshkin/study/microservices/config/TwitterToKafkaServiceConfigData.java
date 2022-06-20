package net.shyshkin.study.microservices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {

    private List<String> twitterKeywords;
    private String welcomeMessage;
    private Mock mock;

    @Data
    public static class Mock {
        private Boolean enable;
        private Long sleepMs;
        private TweetLength tweetLength;
    }

    @Data
    public static class TweetLength {
        private Integer min;
        private Integer max;
    }
}
