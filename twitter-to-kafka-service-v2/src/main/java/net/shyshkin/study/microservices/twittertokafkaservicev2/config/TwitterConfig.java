package net.shyshkin.study.microservices.twittertokafkaservicev2.config;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.twittertokafkaservicev2.executor.TweetsStreamListenersExecutor;
import net.shyshkin.study.microservices.twittertokafkaservicev2.listener.TweetsStreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
    TweetsApi tweetsApi() {
        return twitterApi().tweets();
    }

    @Bean
    TweetsStreamListenersExecutor tweetsStreamListenersExecutor(TweetsStreamListener tweetsStreamListener) {

        var tweetFields = Set.of("author_id", "id", "created_at");
        try {
//            deleteFilteringRules();
            try {
                GetRulesResponse rules = tweetsApi().getRules(null, null, null);
                log.debug("Rules: {}", rules);
            } catch (Exception ex) {
                addFilteringRules();
            }

            InputStream streamResult = tweetsApi().searchStream(5, null, tweetFields, null, null, null, null, 0);

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

    private void addFilteringRules() throws ApiException {

        AddRulesRequest addRulesRequest = new AddRulesRequest();
        List<RuleNoId> ruleNoIds = twitterToKafkaServiceConfigData.getTwitterKeywords()
                .stream()
                .map(keyword -> new RuleNoId().value(keyword))
                .collect(Collectors.toList());
        addRulesRequest.add(ruleNoIds);
        AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest(addRulesRequest);
        AddOrDeleteRulesResponse rulesResponse = tweetsApi().addOrDeleteRules(10, addOrDeleteRulesRequest, false);
        log.debug("Add Rules Response: {}", rulesResponse);
    }

    private void deleteFilteringRules() throws ApiException {

        var deleteRulesRequestDelete = new DeleteRulesRequestDelete();
        twitterToKafkaServiceConfigData
                .getTwitterKeywords()
                .forEach(deleteRulesRequestDelete::addValuesItem);

        AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest(new DeleteRulesRequest().delete(deleteRulesRequestDelete));
        AddOrDeleteRulesResponse rulesResponse = tweetsApi().addOrDeleteRules(addOrDeleteRulesRequest, false);
        log.debug("Delete Rules Response: {}", rulesResponse);
    }

}
