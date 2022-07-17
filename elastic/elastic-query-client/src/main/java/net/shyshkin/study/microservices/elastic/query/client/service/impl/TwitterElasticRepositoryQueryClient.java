package net.shyshkin.study.microservices.elastic.query.client.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticConfigData;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elastic.query.client.exception.ElasticQueryClientException;
import net.shyshkin.study.microservices.elastic.query.client.repository.TwitterElasticsearchQueryRepository;
import net.shyshkin.study.microservices.elastic.query.client.service.ElasticQueryClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "elastic-query-config.use-repository", havingValue = "true")
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private final ElasticConfigData elasticConfigData;
    private final TwitterElasticsearchQueryRepository repository;

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        TwitterIndexModel searchResult = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("No document found at elasticsearch with id `{}`", id);
                    return new ElasticQueryClientException("No document found at elasticsearch with id " + id);
                });
        log.debug("Document with id {} retrieved successfully", searchResult.getId());
        return searchResult;
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> searchResult = repository.findByText(text);
        log.debug("{} documents with text `{}` retrieved successfully", searchResult.size(), text);
        return searchResult;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Iterable<TwitterIndexModel> all = repository.findAll();
        List<TwitterIndexModel> searchResult = StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toList());
        log.debug("{} documents from index `{}` retrieved successfully", searchResult.size(), elasticConfigData.getIndexName());
        return searchResult;
    }
}
