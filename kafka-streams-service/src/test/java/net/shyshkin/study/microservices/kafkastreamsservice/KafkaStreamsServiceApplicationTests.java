package net.shyshkin.study.microservices.kafkastreamsservice;

import net.shyshkin.study.microservices.kafka.client.KafkaAdminClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest
class KafkaStreamsServiceApplicationTests {

    @MockBean
    JwtDecoder jwtDecoder;

    @MockBean
    KafkaAdminClient kafkaAdminClient;

    @Test
    void contextLoads() {
    }

}