package net.shyshkin.study.microservices.twittertokafkaservice.config;

import net.shyshkin.study.microservices.twittertokafkaservice.listener.TwitterKafkaStatusListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Configuration
public class TwitterConfig {

    @Bean
    TwitterStreamFactory twitterStreamFactory() {
        return new TwitterStreamFactory();
    }

    @Bean
    TwitterStream twitterStream(TwitterKafkaStatusListener listener,
                                TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData) {
        TwitterStream twitterStream = twitterStreamFactory().getInstance();
        twitterStream.addListener(listener);

        String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);

        return twitterStream;
    }

}
