package net.shyshkin.study.microservices.reactiveelasticqueryservice.business;


import net.shyshkin.study.microservices.reactiveelasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.model.ElasticQueryServiceResponseModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ElasticQueryService {

    Flux<ElasticQueryServiceResponseModel> getAllDocuments();

    Mono<ElasticQueryServiceResponseModel> getDocumentById(String id);

    Flux<ElasticQueryServiceResponseModel> getDocumentsByText(ElasticQueryServiceRequestModel requestModel);
}
