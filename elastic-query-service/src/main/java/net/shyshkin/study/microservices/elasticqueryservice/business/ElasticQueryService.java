package net.shyshkin.study.microservices.elasticqueryservice.business;

import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceAnalyticsResponseModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceResponseModel;

import java.util.List;

public interface ElasticQueryService {

    List<ElasticQueryServiceResponseModel> getAllDocuments();

    ElasticQueryServiceResponseModel getDocumentById(String id);

    ElasticQueryServiceAnalyticsResponseModel getDocumentsByText(ElasticQueryServiceRequestModel requestModel, String accessToken);
}
