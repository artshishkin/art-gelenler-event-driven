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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                List<String> existingRules = rules.getData().stream()
                        .map(Rule::getValue)
                        .collect(Collectors.toList());
                if (!listsEqual(existingRules, twitterToKafkaServiceConfigData.getTwitterKeywords())) {
                    deleteFilteringRules(existingRules);
                    addFilteringRules();
                }
            } catch (Exception ex) {
                log.error("Exception with Rules", ex);
                addFilteringRules();
            }

            InputStream streamResult = tweetsApi().searchStream(5, null, tweetFields, null, null, null, null, 0);

            // sampleStream with TweetsStreamListenersExecutor
            TweetsStreamListenersExecutor tsle = new TweetsStreamListenersExecutor(streamResult);
            tsle.addListener(tweetsStreamListener);
            return tsle;
        } catch (ApiException e) {
            log.error("Status code: {}. Reason: {}. Response headers: {}", e.getCode(), e.getResponseBody(), e.getResponseHeaders(), e);
        }
        return null;
    }

    private boolean listsEqual(List<?> list1, List<?> list2) {
        if (Objects.equals(list1, list2)) return true;
        if (list1.size() != list2.size()) return false;
        ArrayList<?> list1copy = new ArrayList<>(list1);
        list1copy.removeAll(list2);
        return list1copy.size() == 0;
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

    private void deleteFilteringRules(List<String> rulesToDelete) throws ApiException {

        var deleteRulesRequestDelete = new DeleteRulesRequestDelete();
        rulesToDelete.forEach(deleteRulesRequestDelete::addValuesItem);

        AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest(new DeleteRulesRequest().delete(deleteRulesRequestDelete));
        AddOrDeleteRulesResponse rulesResponse = tweetsApi().addOrDeleteRules(addOrDeleteRulesRequest, false);
        log.debug("Delete Rules Response: {}", rulesResponse);
    }

}
