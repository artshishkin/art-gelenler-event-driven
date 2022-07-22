package net.shyshkin.study.microservices.reactiveelasticqueryservice.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticQueryServiceConfigData;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.business.ReactiveElasticQueryClient;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.repository.TwitterElasticReactiveQueryRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwitterReactiveElasticQueryClient implements ReactiveElasticQueryClient<TwitterIndexModel> {

    private final TwitterElasticReactiveQueryRepository repository;
    private final ElasticQueryServiceConfigData configData;

    @Override
    public Mono<TwitterIndexModel> getIndexModelById(String id) {
        log.debug("Getting data for id: {}", id);
        return repository.findById(id);
    }

    @Override
    public Flux<TwitterIndexModel> getIndexModelByText(String text) {
        log.debug("Getting data for text: {}", text);
        return repository.findByText(text)
                .delayElements(Duration.ofMillis(configData.getBackPressureDelayMs()));
    }

    @Override
    public Flux<TwitterIndexModel> getAllIndexModels() {
        log.debug("Getting all data");
        return repository.findAll();
    }
}
