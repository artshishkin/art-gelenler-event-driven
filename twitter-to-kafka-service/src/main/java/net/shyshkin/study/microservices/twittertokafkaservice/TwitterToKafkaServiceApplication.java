package net.shyshkin.study.microservices.twittertokafkaservice;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"net.shyshkin.study.microservices"})
@EnableEncryptableProperties
public class TwitterToKafkaServiceApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder()
                .environment(new StandardEncryptableEnvironment())
                .sources(TwitterToKafkaServiceApplication.class)
                .run(args);


//        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

}
