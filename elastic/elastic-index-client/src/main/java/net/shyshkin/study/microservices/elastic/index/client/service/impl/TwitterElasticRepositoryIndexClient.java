package net.shyshkin.study.microservices.elastic.index.client.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elastic.index.client.repository.TwitterElasticsearchIndexRepository;
import net.shyshkin.study.microservices.elastic.index.client.service.ElasticIndexClient;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final TwitterElasticsearchIndexRepository repository;

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        Iterable<TwitterIndexModel> modelIterable = repository.saveAll(documents);
        List<String> documentIds = StreamSupport.stream(modelIterable.spliterator(), false)
                .map(TwitterIndexModel::getId)
                .collect(Collectors.toList());
        log.debug("Documents indexed successfully with types: {} and ids: {}",
                TwitterIndexModel.class.getName(),
                documentIds
        );
        return documentIds;
    }
}
