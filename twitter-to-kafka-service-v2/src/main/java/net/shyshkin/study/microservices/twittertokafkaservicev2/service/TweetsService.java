package net.shyshkin.study.microservices.twittertokafkaservicev2.service;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.model.ResourceUnauthorizedProblem;
import com.twitter.clientlib.model.SingleTweetLookupResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TweetsService {

    private final TweetsApi tweetsApi;

    public void fetchTweet() {
        Set<String> tweetFields = new HashSet<>();
        tweetFields.add("author_id");
        tweetFields.add("id");
        tweetFields.add("created_at");

        try {
            // findTweetById
            SingleTweetLookupResponse result = tweetsApi.findTweetById("20", null, tweetFields, null, null, null, null);
            if (result.getErrors() != null && result.getErrors().size() > 0) {
                log.warn("Error:");

                result.getErrors().forEach(e -> {
                    log.warn(e.toString());
                    if (e instanceof ResourceUnauthorizedProblem) {
                        log.warn("{} {}", ((ResourceUnauthorizedProblem) e).getTitle(), ((ResourceUnauthorizedProblem) e).getDetail());
                    }
                });
            } else {
                log.debug("findTweetById - Tweet Text: {}", result);
            }
        } catch (ApiException e) {
            log.error("Status code: {}. Reason: {}. Response headers: {}", e.getCode(), e.getResponseBody(), e.getResponseHeaders(), e);
        }


    }

}
