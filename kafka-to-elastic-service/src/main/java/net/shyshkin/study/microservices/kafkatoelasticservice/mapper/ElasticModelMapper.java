package net.shyshkin.study.microservices.kafkatoelasticservice.mapper;

import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAvroModel;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper
public interface ElasticModelMapper {

    List<TwitterIndexModel> toElasticModels(List<TwitterAvroModel> avroModels);

    default ZonedDateTime map(Long date) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault());
    }

}
