package net.shyshkin.study.microservices.twittertokafkaservicev2.listener;

import com.twitter.clientlib.model.StreamingTweet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TwitterTweetsStreamingListener implements TweetsStreamListener {
    @Override
    public void actionOnTweetsStream(StreamingTweet streamingTweet) {
        if (streamingTweet == null) {
            log.error("Error: actionOnTweetsStream - streamingTweet is null ");
            return;
        }

        if (streamingTweet.getErrors() != null) {
            streamingTweet.getErrors().forEach(problem -> log.debug("{}", problem));
        } else if (streamingTweet.getData() != null) {
            log.debug("New streaming tweet: {}", streamingTweet.getData().getText());
        }
    }
}
