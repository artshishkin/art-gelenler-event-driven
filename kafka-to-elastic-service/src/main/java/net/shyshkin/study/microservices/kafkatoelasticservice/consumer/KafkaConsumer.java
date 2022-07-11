package net.shyshkin.study.microservices.kafkatoelasticservice.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;
import java.util.List;

public interface KafkaConsumer<K extends Serializable, V extends SpecificRecordBase> {

    void receive(List<V> messages, List<Integer> keys, List<Integer> partitions, List<Long> offsets);

}
