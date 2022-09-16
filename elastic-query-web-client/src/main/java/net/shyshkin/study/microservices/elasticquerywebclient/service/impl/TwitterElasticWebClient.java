package net.shyshkin.study.microservices.elasticquerywebclient.service.impl;

import net.shyshkin.study.microservices.config.ElasticQueryWebClientConfigData;
import net.shyshkin.study.microservices.config.WebClientConfigData;
import net.shyshkin.study.microservices.elasticquerywebclient.service.ElasticWebClient;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.exception.ElasticQueryWebClientException;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientResponseModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        WebClientConfigData.Query queryParams = elasticQueryWebClientConfigData.getQueries()
                .get("query-by-text");
        HttpMethod httpMethod = HttpMethod.resolve(queryParams.getMethod());
        Objects.requireNonNull(httpMethod);
        return webClient.method(httpMethod)
                .uri(queryParams.getUri())
                .accept(MediaType.parseMediaType(queryParams.getAccept()))
                .bodyValue(requestModel)
                .retrieve()
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated"))
                )
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(
                                new ElasticQueryWebClientException(clientResponse.statusCode().getReasonPhrase())
                        )
                )
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().getReasonPhrase()))
                )
                .bodyToMono(RESPONSE_MODEL_LIST_TYPE)
                .block();
    }
}
