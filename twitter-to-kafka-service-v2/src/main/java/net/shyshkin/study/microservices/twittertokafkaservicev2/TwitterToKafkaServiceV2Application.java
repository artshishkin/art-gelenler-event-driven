package net.shyshkin.study.microservices.twittertokafkaservicev2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class TwitterToKafkaServiceV2Application {

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceV2Application.class, args);
    }

}
