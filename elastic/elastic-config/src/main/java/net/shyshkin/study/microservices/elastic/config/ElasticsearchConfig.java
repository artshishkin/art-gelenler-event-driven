package net.shyshkin.study.microservices.elastic.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.config.ElasticConfigData;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    private final ElasticConfigData elasticConfigData;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        UriComponents serverUri = UriComponentsBuilder
                .fromUriString(elasticConfigData.getConnectionUrl())
                .build();
        HttpHost httpHost = new HttpHost(
                Objects.requireNonNull(serverUri.getHost()),
                serverUri.getPort(),
                serverUri.getScheme()
        );
        RestClientBuilder restClientBuilder = RestClient
                .builder(httpHost)
                .setRequestConfigCallback(
                        requestConfigBuilder -> requestConfigBuilder
                                .setConnectTimeout(elasticConfigData.getConnectionTimeoutMs())
                                .setSocketTimeout(elasticConfigData.getSocketTimeoutMs())
                );
        return new RestHighLevelClient(restClientBuilder);
    }
}
