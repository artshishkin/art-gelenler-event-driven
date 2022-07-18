package net.shyshkin.study.microservices.elasticqueryservice.mapper;

import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TwitterIndexMapper {

    ElasticQueryServiceResponseModel toResponseModel(TwitterIndexModel twitterIndexModel);

    List<ElasticQueryServiceResponseModel> toResponseModelList(List<TwitterIndexModel> twitterIndexModelList);

}
