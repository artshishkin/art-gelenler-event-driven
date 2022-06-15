package net.shyshkin.study.microservices.twittertokafkaservicev2.listener;

import com.twitter.clientlib.model.StreamingTweet;
import org.springframework.stereotype.Component;

@Component
public class TwitterTweetsStreamingListener implements TweetsStreamListener{
    @Override
    public void actionOnTweetsStream(StreamingTweet streamingTweet) {
        if(streamingTweet == null) {
            System.err.println("Error: actionOnTweetsStream - streamingTweet is null ");
            return;
        }

        if(streamingTweet.getErrors() != null) {
            streamingTweet.getErrors().forEach(System.out::println);
        } else if (streamingTweet.getData() != null) {
            System.out.println("New streaming tweet: " + streamingTweet.getData().getText());
        }
    }
}
