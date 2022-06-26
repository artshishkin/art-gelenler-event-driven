package net.shyshkin.study.microservices.twittertokafkaservice.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.twittertokafkaservice.runner.StreamRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationInitialization implements CommandLineRunner {

    private final StreamRunner streamRunner;
    private final StreamInitializer streamInitializer;

    @Override
    public void run(String... args) throws Exception {
        log.debug("App starts...");
        streamInitializer.init();
        streamRunner.start();
    }
}
