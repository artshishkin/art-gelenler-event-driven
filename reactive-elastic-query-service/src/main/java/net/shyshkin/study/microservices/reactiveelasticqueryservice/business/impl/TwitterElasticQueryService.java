package net.shyshkin.study.microservices.reactiveelasticqueryservice.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.business.ElasticQueryService;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.business.ReactiveElasticQueryClient;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.mapper.TwitterIndexMapper;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.model.ElasticQueryServiceResponseModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ReactiveElasticQueryClient<TwitterIndexModel> elasticQueryClient;
    private final TwitterIndexMapper twitterIndexMapper;

    @Override
    public Flux<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.debug("Querying Elasticsearch for all the documents");
        return elasticQueryClient.getAllIndexModels()
                .map(twitterIndexMapper::toResponseModel);
    }

    @Override
    public Mono<ElasticQueryServiceResponseModel> getDocumentById(String id) {
        log.debug("Querying Elasticsearch for document by id `{}`", id);
        return elasticQueryClient.getIndexModelById(id)
                .map(twitterIndexMapper::toResponseModel);
    }

    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentsByText(ElasticQueryServiceRequestModel requestModel) {
        String searchText = requestModel.getText();
        log.debug("Querying Elasticsearch for document by text `{}`", searchText);
        return elasticQueryClient.getIndexModelByText(searchText)
                .map(twitterIndexMapper::toResponseModel);
    }
}
