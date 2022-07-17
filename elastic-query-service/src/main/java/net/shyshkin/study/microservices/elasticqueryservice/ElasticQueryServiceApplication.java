package net.shyshkin.study.microservices.elasticqueryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"net.shyshkin.study.microservices"})
public class ElasticQueryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticQueryServiceApplication.class, args);
    }

}
