package net.shyshkin.study.microservices.twittertokafkaservice.runner.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.TwitterToKafkaServiceConfigData;
import net.shyshkin.study.microservices.twittertokafkaservice.exception.TwitterToKafkaServiceException;
import net.shyshkin.study.microservices.twittertokafkaservice.runner.StreamRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import javax.annotation.PreDestroy;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "twitter-to-kafka-service.mock.enable", havingValue = "true", matchIfMissing = false)
public class MockTwitterStreamRunner implements StreamRunner {

    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final StatusListener statusListener;

    private static final String TWEET_AS_RAW_JSON_PATTERN = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    private static final String[] WORDS = new String[]{
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetuer",
            "adipiscing",
            "elit",
            "Maecenas",
            "porttitor",
            "congue",
            "massa",
            "Fusce",
            "posuere",
            "magna",
            "sed",
            "pulvinar",
            "ultricies",
            "purus",
            "lectus",
            "malesuada",
            "libero"
    };

    @Override
    public void start() throws TwitterException {
        var tweetLength = twitterToKafkaServiceConfigData.getMock().getTweetLength();
        var sleepMs = twitterToKafkaServiceConfigData.getMock().getSleepMs();
        var keywords = twitterToKafkaServiceConfigData.getTwitterKeywords();

        log.debug("Starting mock filtering twitter streams for keywords {}", keywords);
        simulateTwitterStream(tweetLength, sleepMs, keywords);
    }

    private void simulateTwitterStream(TwitterToKafkaServiceConfigData.TweetLength tweetLength, Long sleepMs, List<String> keywords) {
        Executors.newSingleThreadExecutor()
                .submit(() -> {
                    try {
                        while (true) {
                            String formattedStringAsRawJson = getFormattedTweet(keywords, tweetLength);
                            Status status = TwitterObjectFactory.createStatus(formattedStringAsRawJson);
                            statusListener.onStatus(status);
                            sleep(sleepMs);
                        }
                    } catch (TwitterException e) {
                        log.error("Error creating Twitter Status", e);
                    }
                });
    }

    private void sleep(long sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            throw new TwitterToKafkaServiceException("Error while sleeping for waiting new status to create!!");
        }
    }

    private String getFormattedTweet(List<String> keywords, TwitterToKafkaServiceConfigData.TweetLength tweetLength) {

        var params = new String[]{
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE)),
                constructRandomTweet(keywords, tweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE))
        };

        String tweet = TWEET_AS_RAW_JSON_PATTERN;
        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private String constructRandomTweet(List<String> keywords, TwitterToKafkaServiceConfigData.TweetLength tweetLength) {
        int useTweetLength = ThreadLocalRandom.current().nextInt(tweetLength.getMin(), tweetLength.getMax());
        int keywordIndex = ThreadLocalRandom.current().nextInt(keywords.size());
        String keyword = keywords.get(keywordIndex);
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < useTweetLength; i++) {
            String nextWord = WORDS[ThreadLocalRandom.current().nextInt(WORDS.length)];
            joiner.add(nextWord);
            if (i == useTweetLength / 2) joiner.add(keyword);
        }
        return joiner.toString();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Closing twitter stream!");
    }

}
