package net.shyshkin.study.microservices.elastic.query.client.util;

import net.shyshkin.study.microservices.elastic.model.index.IndexModel;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class ElasticQueryUtil<T extends IndexModel> {

    public Query getSearchQueryById(String id) {
        return new NativeSearchQueryBuilder()
                .withIds(id)
                .build();
    }

    public Query getSearchQueryByFieldText(String field, String text) {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(field, text))
                .build();
    }

    public Query getSearchQueryForAll() {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
    }

}
