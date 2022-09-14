package net.shyshkin.study.microservices.kafkastreamsservice.runner.impl;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.config.KafkaConfigData;
import net.shyshkin.study.microservices.config.KafkaStreamsConfigData;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAnalyticsAvroModel;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAvroModel;
import net.shyshkin.study.microservices.kafkastreamsservice.runner.StreamsRunner;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class KafkaStreamsRunner implements StreamsRunner<String, Long> {

    private static final String REGEX = "\\W+";

    private final KafkaStreamsConfigData kafkaStreamsConfigData;
    private final KafkaConfigData kafkaConfigData;
    private final Properties streamsConfiguration;
    private KafkaStreams kafkaStreams;

    private volatile ReadOnlyKeyValueStore<String, Long> keyValueStore;

    @Override
    public void start() {

        var schemaRegistry = kafkaConfigData.getSchemaRegistry();
        var serdeConfig = Map.of(schemaRegistry.getKey(), schemaRegistry.getUrl());
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<Long, TwitterAvroModel> twitterAvroModelKStream = getTwitterAvroModelKStream(serdeConfig, streamsBuilder);
        log.debug("{}", twitterAvroModelKStream);

        createTopology(twitterAvroModelKStream, serdeConfig);

        startStreaming(streamsBuilder);
    }

    @Override
    public Long getValueByKey(String word) {
        if (kafkaStreams != null && kafkaStreams.state() == KafkaStreams.State.RUNNING) {
            if (keyValueStore == null) {
                synchronized (this) {
                    if (keyValueStore == null) {
                        keyValueStore = kafkaStreams.store(
                                StoreQueryParameters.fromNameAndType(
                                        kafkaStreamsConfigData.getWordCountStoreName(),
                                        QueryableStoreTypes.keyValueStore()
                                )
                        );
                    }
                }
            }
            return keyValueStore.get(word.toLowerCase());
        }
        return 0L;
    }

    @PreDestroy
    public void close() {
        if (kafkaStreams != null) {
            kafkaStreams.close();
            log.debug("Kafka streaming closed!");
        }
    }

    private void startStreaming(StreamsBuilder streamsBuilder) {
        final Topology topology = streamsBuilder.build();
        log.debug("Defined topology: {}", topology.describe());

        kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
        kafkaStreams.start();
        log.debug("Kafka streaming started");
    }

    private KStream<Long, TwitterAvroModel> getTwitterAvroModelKStream(Map<String, String> serdeConfig, StreamsBuilder streamsBuilder) {
        Serde<TwitterAvroModel> serdeTwitterAvroModel = new SpecificAvroSerde<>();
        serdeTwitterAvroModel.configure(serdeConfig, false);
        return streamsBuilder.stream(kafkaStreamsConfigData.getInputTopicName(), Consumed.with(Serdes.Long(), serdeTwitterAvroModel));
    }

    private void createTopology(KStream<Long, TwitterAvroModel> twitterAvroModelKStream, Map<String, String> serdeConfig) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.UNICODE_CHARACTER_CLASS);

        Serde<TwitterAnalyticsAvroModel> serdeTwitterAnalyticsAvroModel = getSerdeAnalyticsModel(serdeConfig);

        twitterAvroModelKStream
                .flatMapValues(value -> List.of(pattern.split(value.getText().toLowerCase())))
                .groupBy((key, word) -> word)
                .count(Materialized.as(kafkaStreamsConfigData.getWordCountStoreName()))
                .toStream()
                .map(toAnalyticsModel())
                .to(
                        kafkaStreamsConfigData.getOutputTopicName(),
                        Produced.with(Serdes.String(), serdeTwitterAnalyticsAvroModel)
                );
    }

    private KeyValueMapper<String, Long, KeyValue<? extends String, ? extends TwitterAnalyticsAvroModel>> toAnalyticsModel() {
        return (word, count) -> {
            log.debug("Sending to topic {}, word {} - count {}", kafkaStreamsConfigData.getOutputTopicName(), word, count);
            var model = TwitterAnalyticsAvroModel.newBuilder()
                    .setWord(word)
                    .setWordCount(count)
                    .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .build();
            return new KeyValue<>(word, model);
        };
    }

    private Serde<TwitterAnalyticsAvroModel> getSerdeAnalyticsModel(Map<String, String> serdeConfig) {
        Serde<TwitterAnalyticsAvroModel> serde = new SpecificAvroSerde<>();
        serde.configure(serdeConfig, false);
        return serde;
    }

}
