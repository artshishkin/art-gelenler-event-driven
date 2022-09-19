package net.shyshkin.study.microservices.elasticqueryservice.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticQueryServiceConfigData;
import net.shyshkin.study.microservices.config.WebClientConfigData;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elastic.query.client.service.ElasticQueryClient;
import net.shyshkin.study.microservices.elasticqueryservice.QueryType;
import net.shyshkin.study.microservices.elasticqueryservice.business.ElasticQueryService;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceAnalyticsResponseModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceWordCountResponseModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.assembler.ElasticQueryServiceResponseModelAssembler;
import net.shyshkin.study.microservices.elasticqueryservicecommon.exception.ElasticQueryServiceException;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceResponseModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;
    private final ElasticQueryServiceResponseModelAssembler assembler;
    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.debug("Querying Elasticsearch for all the documents");
        List<TwitterIndexModel> indexModels = elasticQueryClient.getAllIndexModels();
        return assembler.toModels(indexModels);
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        log.debug("Querying Elasticsearch for document by id `{}`", id);
        TwitterIndexModel indexModel = elasticQueryClient.getIndexModelById(id);
        return assembler.toModel(indexModel);
    }

    @Override
    public ElasticQueryServiceAnalyticsResponseModel getDocumentsByText(ElasticQueryServiceRequestModel requestModel, String accessToken) {
        String searchText = requestModel.getText();
        log.debug("Querying Elasticsearch for document by text `{}`", searchText);
        List<TwitterIndexModel> indexModelList = elasticQueryClient.getIndexModelByText(searchText);
        List<ElasticQueryServiceResponseModel> elasticQueryServiceResponseModels = assembler.toModels(indexModelList);
        return ElasticQueryServiceAnalyticsResponseModel.builder()
                .queryResponseModels(elasticQueryServiceResponseModels)
                .wordCount(getWordCount(searchText, accessToken))
                .build();
    }

    private Long getWordCount(String text, String accessToken) {
        if (QueryType.KAFKA_STATE_STORE.getType().equals(elasticQueryServiceConfigData.getWebclient().getQueryType())) {
            return getFromKafkaStateStore(text, accessToken).getWordCount();
        }
        return null;
    }

    private ElasticQueryServiceWordCountResponseModel getFromKafkaStateStore(String text, String accessToken) {
        WebClientConfigData.Query query = elasticQueryServiceConfigData.getQueries().get("get-word-count-by-word");
        return retrieveResponseModel(text, accessToken, query);
    }

    private ElasticQueryServiceWordCountResponseModel retrieveResponseModel(String text, String accessToken, WebClientConfigData.Query query) {
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(query.getMethod()))
                .uri(query.getUri(), uriBuilder -> uriBuilder.build(text))
                .headers(h -> h.setBearerAuth(accessToken))
                .accept(MediaType.valueOf(query.getAccept()))
                .retrieve()
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated"))
                )
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(new ElasticQueryServiceException(clientResponse.statusCode().getReasonPhrase()))
                )
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().getReasonPhrase()))
                )
                .bodyToMono(ElasticQueryServiceWordCountResponseModel.class)
                .log()
                .block();
    }
}
