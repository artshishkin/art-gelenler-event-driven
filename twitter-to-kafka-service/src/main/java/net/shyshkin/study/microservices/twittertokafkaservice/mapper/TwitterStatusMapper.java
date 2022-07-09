package net.shyshkin.study.microservices.twittertokafkaservice.mapper;

import net.shyshkin.study.microservices.kafka.avro.model.TwitterAvroModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import twitter4j.Status;

@Mapper
public interface TwitterStatusMapper {

    @Mapping(target = "createdAt", source = "createdAt.time")
    @Mapping(target = "userId", source = "user.id")
    TwitterAvroModel toModel(Status status);

}
