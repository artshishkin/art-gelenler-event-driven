package net.shyshkin.study.microservices.twittertokafkaservicev2.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.twittertokafkaservicev2.config.TwitterToKafkaServiceConfigData;
import net.shyshkin.study.microservices.twittertokafkaservicev2.executor.TweetsStreamListenersExecutor;
import net.shyshkin.study.microservices.twittertokafkaservicev2.service.TweetsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationInitialization implements CommandLineRunner {

    private final TwitterToKafkaServiceConfigData configData;
    private final TweetsService tweetsService;
    private final TweetsStreamListenersExecutor tweetsStreamListenersExecutor;

    @Override
    public void run(String... args) throws Exception {
        log.debug("{}", configData.getWelcomeMessage());
        log.debug("Keywords: {}", configData.getTwitterKeywords());
//        tweetsService.fetchTweet();
        tweetsStreamListenersExecutor.executeListeners();
    }
}
