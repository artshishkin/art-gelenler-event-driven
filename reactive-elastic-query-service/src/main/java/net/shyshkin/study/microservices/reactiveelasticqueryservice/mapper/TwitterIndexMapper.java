package net.shyshkin.study.microservices.reactiveelasticqueryservice.mapper;

import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.model.ElasticQueryServiceResponseModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TwitterIndexMapper {

    ElasticQueryServiceResponseModel toResponseModel(TwitterIndexModel twitterIndexModel);

    List<ElasticQueryServiceResponseModel> toResponseModelList(List<TwitterIndexModel> twitterIndexModelList);

}
