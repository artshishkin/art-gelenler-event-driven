package net.shyshkin.study.microservices.elastic.index.client.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.ElasticConfigData;
import net.shyshkin.study.microservices.elastic.index.client.service.ElasticIndexClient;
import net.shyshkin.study.microservices.elastic.index.client.util.ElasticIndexUtil;
import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ConditionalOnProperty(name = "elastic-config.use-repository", havingValue = "false", matchIfMissing = true)
@Service
@RequiredArgsConstructor
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final ElasticConfigData elasticConfigData;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
        List<IndexedObjectInformation> indexedObjectInformations = elasticsearchOperations
                .bulkIndex(
                        indexQueries,
                        IndexCoordinates.of(elasticConfigData.getIndexName())
                );
        List<String> documentIds = indexedObjectInformations.stream()
                .map(IndexedObjectInformation::getId)
                .collect(Collectors.toList());
        log.debug("Documents indexed successfully with types: {} and ids: {}",
                TwitterIndexModel.class.getName(),
                documentIds
        );
        return documentIds;
    }

}
