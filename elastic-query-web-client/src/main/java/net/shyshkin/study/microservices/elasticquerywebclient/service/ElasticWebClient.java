package net.shyshkin.study.microservices.elasticquerywebclient.service;

import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientAnalyticsResponseModel;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientRequestModel;

public interface ElasticWebClient {

    ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
