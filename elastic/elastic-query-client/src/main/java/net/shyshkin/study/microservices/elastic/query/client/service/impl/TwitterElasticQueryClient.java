package net.shyshkin.study.microservices.elastic.query.client.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticConfigData;
import net.shyshkin.study.microservices.config.ElasticQueryConfigData;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elastic.query.client.exception.ElasticQueryClientException;
import net.shyshkin.study.microservices.elastic.query.client.service.ElasticQueryClient;
import net.shyshkin.study.microservices.elastic.query.client.util.ElasticQueryUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "elastic-query-config.use-repository", havingValue = "false", matchIfMissing = true)
public class TwitterElasticQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private final ElasticConfigData elasticConfigData;
    private final ElasticQueryConfigData elasticQueryConfigData;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil;

    private IndexCoordinates twitterIndexCoordinates;

    @PostConstruct
    void init() {
        twitterIndexCoordinates = IndexCoordinates.of(elasticConfigData.getIndexName());
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Query query = elasticQueryUtil.getSearchQueryById(id);
        SearchHit<TwitterIndexModel> searchHit = elasticsearchOperations.searchOne(query, TwitterIndexModel.class, twitterIndexCoordinates);
        if (searchHit == null) {
            log.error("No document found at elasticsearch with id `{}`", id);
            throw new ElasticQueryClientException("No document found at elasticsearch with id " + id);
        }
        log.debug("Document with id {} retrieved successfully", searchHit.getId());
        return searchHit.getContent();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        Query query = elasticQueryUtil.getSearchQueryByFieldText(elasticQueryConfigData.getTextField(), text);
        return search(query, "{} documents with text `{}` retrieved successfully", text);
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Query query = elasticQueryUtil.getSearchQueryForAll();
        return search(query, "{} documents from index `{}` retrieved successfully", twitterIndexCoordinates.getIndexName());
    }

    private List<TwitterIndexModel> search(Query query, String logMessage, Object... logParams) {
        SearchHits<TwitterIndexModel> hits = elasticsearchOperations.search(query, TwitterIndexModel.class, twitterIndexCoordinates);
        log.debug(logMessage, hits.getTotalHits(), logParams);
        return hits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

}
