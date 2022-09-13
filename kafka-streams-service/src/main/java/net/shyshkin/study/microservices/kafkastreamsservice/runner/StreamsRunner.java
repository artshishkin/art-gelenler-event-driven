package net.shyshkin.study.microservices.kafkastreamsservice.runner;

public interface StreamsRunner<K, V> {
    void start();
    default V getValueByKey(K key) {
        return null;
    }
}