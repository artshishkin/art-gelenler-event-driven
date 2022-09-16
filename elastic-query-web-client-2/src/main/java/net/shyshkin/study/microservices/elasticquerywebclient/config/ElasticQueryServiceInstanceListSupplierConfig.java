package net.shyshkin.study.microservices.elasticquerywebclient.config;

import net.shyshkin.study.microservices.config.ElasticQueryWebClientConfigData;
import net.shyshkin.study.microservices.config.WebClientConfigData;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Primary
public class ElasticQueryServiceInstanceListSupplierConfig implements ServiceInstanceListSupplier {

    private final WebClientConfigData webClientConfigData;

    public ElasticQueryServiceInstanceListSupplierConfig(ElasticQueryWebClientConfigData elasticQueryWebClientConfigData) {
        this.webClientConfigData = elasticQueryWebClientConfigData.getWebclient();
    }

    @Override
    public String getServiceId() {
        return webClientConfigData.getServiceId();
    }

    @Override
    public Flux<List<ServiceInstance>> get() {

        List<ServiceInstance> defaultServiceInstances = webClientConfigData.getInstances()
                .stream()
                .map(instance -> new DefaultServiceInstance(
                        instance.getId(),
                        this.getServiceId(),
                        instance.getHost(),
                        instance.getPort(),
                        false))
                .collect(Collectors.toList());

        return Flux.just(defaultServiceInstances);
    }
}
