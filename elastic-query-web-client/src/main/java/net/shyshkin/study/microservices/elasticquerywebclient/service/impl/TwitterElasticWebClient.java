package net.shyshkin.study.microservices.elasticquerywebclient.service.impl;

import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientResponseModel;
import net.shyshkin.study.microservices.elasticquerywebclient.service.ElasticWebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TwitterElasticWebClient implements ElasticWebClient {

    public static final ParameterizedTypeReference<List<ElasticQueryWebClientResponseModel>> RESPONSE_MODEL_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;

    public TwitterElasticWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        return webClient.post()
                .uri("/get-document-by-text")
                .bodyValue(requestModel)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(RESPONSE_MODEL_LIST_TYPE))
                .block();
    }
}
