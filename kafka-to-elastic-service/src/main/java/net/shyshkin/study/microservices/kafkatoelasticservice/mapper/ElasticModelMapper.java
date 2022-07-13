package net.shyshkin.study.microservices.kafkatoelasticservice.mapper;

import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAvroModel;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper
public interface ElasticModelMapper {

    List<TwitterIndexModel> toElasticModels(List<TwitterAvroModel> avroModels);

    default LocalDateTime map(Long date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault());
    }

}
