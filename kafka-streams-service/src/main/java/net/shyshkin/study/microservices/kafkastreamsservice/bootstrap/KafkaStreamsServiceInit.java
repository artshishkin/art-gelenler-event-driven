package net.shyshkin.study.microservices.kafkastreamsservice.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.kafkastreamsservice.init.StreamsInitializer;
import net.shyshkin.study.microservices.kafkastreamsservice.runner.StreamsRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamsServiceInit implements CommandLineRunner {

    private final StreamsRunner<String, Long> streamsRunner;
    private final StreamsInitializer streamsInitializer;

    @Override
    public void run(String... args) throws Exception {
        log.info("App starts...");
        streamsInitializer.init();
        streamsRunner.start();
    }
}
