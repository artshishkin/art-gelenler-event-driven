package net.shyshkin.study.microservices.twittertokafkaservicev2.listener;

import com.twitter.clientlib.model.StreamingTweet;

public interface TweetsStreamListener {
    void actionOnTweetsStream(StreamingTweet streamingTweet);
}
