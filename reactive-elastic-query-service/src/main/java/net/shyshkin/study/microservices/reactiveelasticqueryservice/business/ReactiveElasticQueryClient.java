package net.shyshkin.study.microservices.reactiveelasticqueryservice.business;

import net.shyshkin.study.microservices.elastic.model.index.IndexModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveElasticQueryClient<T extends IndexModel> {

    Mono<T> getIndexModelById(String id);

    Flux<T> getIndexModelByText(String text);

    Flux<T> getAllIndexModels();

}
