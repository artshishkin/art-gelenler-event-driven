package net.shyshkin.study.microservices.reactiveelasticqueryservice.repository;

import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TwitterElasticReactiveQueryRepository extends ReactiveElasticsearchRepository<TwitterIndexModel, String> {

    Flux<TwitterIndexModel> findByText(String text);

}
