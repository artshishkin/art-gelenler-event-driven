package net.shyshkin.study.microservices.kafkastreamsservice.runner.impl;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.kafkastreamsservice.runner.StreamsRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestStreamsRunner implements StreamsRunner<String, Long> {
    @Override
    public void start() {
        log.debug("Start test runner");
    }
}
