package net.shyshkin.study.microservices.elasticquerywebclient.service;

import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientResponseModel;

import java.util.List;

public interface ElasticWebClient {

    List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
