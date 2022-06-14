package net.shyshkin.study.microservices.twittertokafkaservice.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.twittertokafkaservice.config.TwitterToKafkaServiceConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationInitialization implements CommandLineRunner {

    private final TwitterToKafkaServiceConfigData configData;

    @Override
    public void run(String... args) throws Exception {
        log.debug("{}", configData.getWelcomeMessage());
        log.debug("Keywords: {}", configData.getTwitterKeywords());
    }
}
