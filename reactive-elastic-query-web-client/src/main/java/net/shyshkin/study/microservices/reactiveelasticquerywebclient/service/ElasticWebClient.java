package net.shyshkin.study.microservices.reactiveelasticquerywebclient.service;


import net.shyshkin.study.microservices.reactiveelasticquerywebclient.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.reactiveelasticquerywebclient.model.ElasticQueryWebClientResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticWebClient {

    Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
