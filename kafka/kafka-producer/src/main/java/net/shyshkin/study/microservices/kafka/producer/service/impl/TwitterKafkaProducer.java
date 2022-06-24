package net.shyshkin.study.microservices.kafka.producer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAvroModel;
import net.shyshkin.study.microservices.kafka.producer.service.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PreDestroy;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {

    private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    @Override
    public void sent(String topicName, Long key, TwitterAvroModel message) {
        log.debug("Sending message `{}` to topic `{}`", message, topicName);
        ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaSendFuture = kafkaTemplate.send(topicName, key, message);
        kafkaSendFuture.addCallback(
                result -> {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.debug("Received new metadata. Topic: {}: Partition: {}: Offset: {}: Timestamp: {}, at time {}",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp(),
                            System.nanoTime()
                    );
                },
                ex -> log.error("Error while sending message {} to topic: {}", message, topicName, ex));
    }

    @PreDestroy
    public void close(){
        if (kafkaTemplate!=null){
            log.debug("Closing Kafka Producer");
            kafkaTemplate.destroy();
        }
    }

}
