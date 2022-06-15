package net.shyshkin.study.microservices.twittertokafkaservicev2.config;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.twittertokafkaservicev2.executor.TweetsStreamListenersExecutor;
import net.shyshkin.study.microservices.twittertokafkaservicev2.listener.TweetsStreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class TwitterConfig {

    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

    @Bean
    TwitterApi twitterApi() {
        TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(twitterToKafkaServiceConfigData.getTwitterV2BearerToken());
        TwitterApi apiInstance = new TwitterApi();
        apiInstance.setTwitterCredentials(credentials);
        return apiInstance;
    }

    @Bean
    TweetsStreamListenersExecutor tweetsStreamListenersExecutor(TweetsStreamListener tweetsStreamListener) {

        var tweetFields = Set.of("author_id", "id", "created_at");
        try {
            InputStream streamResult = twitterApi().tweets().sampleStream(null, tweetFields, null, null, null, null, 0);
            // sampleStream with TweetsStreamListenersExecutor

            TweetsStreamListenersExecutor tsle = new TweetsStreamListenersExecutor(streamResult);
            tsle.addListener(tweetsStreamListener);
            return tsle;
        } catch (ApiException e) {
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
        return null;
    }

}
