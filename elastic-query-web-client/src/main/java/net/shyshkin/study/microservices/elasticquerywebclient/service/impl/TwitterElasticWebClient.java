package net.shyshkin.study.microservices.elasticquerywebclient.service.impl;

import net.shyshkin.study.microservices.config.ElasticQueryWebClientConfigData;
import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientResponseModel;
import net.shyshkin.study.microservices.elasticquerywebclient.service.ElasticWebClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
public class TwitterElasticWebClient implements ElasticWebClient {

    public static final ParameterizedTypeReference<List<ElasticQueryWebClientResponseModel>> RESPONSE_MODEL_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    public TwitterElasticWebClient(WebClient.Builder webClientBuilder, ElasticQueryWebClientConfigData elasticQueryWebClientConfigData) {
        this.webClient = webClientBuilder.build();
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
    }

    @Override
    public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        ElasticQueryWebClientConfigData.Query queryParams = elasticQueryWebClientConfigData.getQueries()
                .get("query-by-text");
        HttpMethod httpMethod = HttpMethod.resolve(queryParams.getMethod());
        Objects.requireNonNull(httpMethod);
        return webClient.method(httpMethod)
                .uri(queryParams.getUri())
                .accept(MediaType.parseMediaType(queryParams.getAccept()))
                .bodyValue(requestModel)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(RESPONSE_MODEL_LIST_TYPE))
                .block();
    }
}
