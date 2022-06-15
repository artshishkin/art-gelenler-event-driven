package net.shyshkin.study.microservices.twittertokafkaservice.runner.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.twittertokafkaservice.runner.StreamRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

import javax.annotation.PreDestroy;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "twitter-to-kafka-service.mock.enable", havingValue = "false", matchIfMissing = true)
public class TwitterKafkaStreamRunner implements StreamRunner {

    private TwitterStream twitterStream;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void start() throws TwitterException {
        twitterStream = applicationContext.getBean(TwitterStream.class);
    }

    @PreDestroy
    public void shutdown() {
        if (twitterStream != null) {
            log.info("Closing twitter stream!");
            twitterStream.shutdown();
        }
    }

}
