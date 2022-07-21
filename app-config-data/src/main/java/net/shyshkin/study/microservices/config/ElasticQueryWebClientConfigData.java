package net.shyshkin.study.microservices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-query-web-client")
public class ElasticQueryWebClientConfigData {

    private Webclient webclient;
    private Map<String, Query> queries;

    @Data
    public static class Webclient {
        private Integer connectTimeoutMs;
        private Long readTimeoutMs;
        private Long writeTimeoutMs;
        private Integer maxInMemorySize;
        private String contentType;
        private String acceptType;
        private URI baseUrl;
        private String serviceId;
        private List<Instance> instances;
    }

    @Data
    public static class Query {
        private String method;
        private String uri;
        private String accept;
    }

    @Data
    public static class Instance {
        private String id;
        private String host;
        private Integer port;
    }

}
