package net.shyshkin.study.microservices.config;

import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class WebClientConfigData {
    private Integer connectTimeoutMs;
    private Long readTimeoutMs;
    private Long writeTimeoutMs;
    private Integer maxInMemorySize;
    private String contentType;
    private String acceptType;
    private URI baseUrl;
    private String serviceId;
    private String queryType;
    private List<Instance> instances;

    @Data
    public static class Instance {
        private String id;
        private String host;
        private Integer port;
    }

    @Data
    public static class Query {
        private String method;
        private String uri;
        private String accept;
    }
}
