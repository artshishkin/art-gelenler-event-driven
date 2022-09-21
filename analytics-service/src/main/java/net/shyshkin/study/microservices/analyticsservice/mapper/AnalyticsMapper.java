package net.shyshkin.study.microservices.analyticsservice.mapper;

import net.shyshkin.study.microservices.analyticsservice.dataaccess.entity.AnalyticsEntity;
import net.shyshkin.study.microservices.analyticsservice.model.AnalyticsResponseModel;
import net.shyshkin.study.microservices.kafka.avro.model.TwitterAnalyticsAvroModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.IdGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Mapper
public abstract class AnalyticsMapper {

    @Autowired
    protected IdGenerator idGenerator;

    public abstract List<AnalyticsEntity> toEntityList(List<TwitterAnalyticsAvroModel> avroModels);

    @Mapping(target = "id", expression = "java(idGenerator.generateId())")
    @Mapping(target = "recordDate", source = "createdAt")
    public abstract AnalyticsEntity toEntity(TwitterAnalyticsAvroModel avroModel);

    LocalDateTime mapRecordDate(Long createdAt) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAt), ZoneOffset.UTC);
    }

    public Optional<AnalyticsResponseModel> toResponseModel(AnalyticsEntity entity) {
        return Optional.ofNullable(getResponseModel(entity));
    }

    abstract AnalyticsResponseModel getResponseModel(AnalyticsEntity entity);

}
