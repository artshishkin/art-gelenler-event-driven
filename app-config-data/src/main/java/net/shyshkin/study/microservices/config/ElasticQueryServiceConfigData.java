package net.shyshkin.study.microservices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-query-service")
public class ElasticQueryServiceConfigData {

    private String version;
    private Long backPressureDelayMs;
    private String customAudience;
    private WebClientConfigData webclient;
    private Map<String, WebClientConfigData.Query> queries;

}
