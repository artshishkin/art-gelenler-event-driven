package net.shyshkin.study.microservices.elasticqueryservice.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elastic.query.client.service.ElasticQueryClient;
import net.shyshkin.study.microservices.elasticqueryservice.business.ElasticQueryService;
import net.shyshkin.study.microservices.elasticqueryservice.mapper.TwitterIndexMapper;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;
    private final TwitterIndexMapper mapper;

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.debug("Querying Elasticsearch for all the documents");
        List<TwitterIndexModel> indexModels = elasticQueryClient.getAllIndexModels();
        return mapper.toResponseModelList(indexModels);
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        log.debug("Querying Elasticsearch for document by id `{}`", id);
        TwitterIndexModel indexModel = elasticQueryClient.getIndexModelById(id);
        return mapper.toResponseModel(indexModel);
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(ElasticQueryServiceRequestModel requestModel) {
        String searchText = requestModel.getText();
        log.debug("Querying Elasticsearch for document by text `{}`", searchText);
        List<TwitterIndexModel> indexModelList = elasticQueryClient.getIndexModelByText(searchText);
        return mapper.toResponseModelList(indexModelList);
    }
}
