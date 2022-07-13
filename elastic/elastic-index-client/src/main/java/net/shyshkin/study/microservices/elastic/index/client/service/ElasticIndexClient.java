package net.shyshkin.study.microservices.elastic.index.client.service;

import net.shyshkin.study.microservices.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient<T extends IndexModel> {

    List<String> save(List<T> documents);

}
