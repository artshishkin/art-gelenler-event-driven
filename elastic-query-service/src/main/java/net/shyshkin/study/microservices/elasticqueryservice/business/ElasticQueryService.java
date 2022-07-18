package net.shyshkin.study.microservices.elasticqueryservice.business;

import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModel;

import java.util.List;

public interface ElasticQueryService {

    List<ElasticQueryServiceResponseModel> getAllDocuments();

    ElasticQueryServiceResponseModel getDocumentById(String id);

    List<ElasticQueryServiceResponseModel> getDocumentsByText(ElasticQueryServiceRequestModel requestModel);
}
