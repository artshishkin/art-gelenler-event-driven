package net.shyshkin.study.microservices.kafka.producer.service;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {

    void sent(String topicName, K key, V message);

}
