package net.shyshkin.study.microservices.reactiveelasticquerywebclient.service;


import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticWebClient {

    Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
