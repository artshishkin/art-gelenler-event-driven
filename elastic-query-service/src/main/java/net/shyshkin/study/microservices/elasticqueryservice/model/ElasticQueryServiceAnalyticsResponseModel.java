package net.shyshkin.study.microservices.elasticqueryservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceResponseModel;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceAnalyticsResponseModel {

    private List<ElasticQueryServiceResponseModel> queryResponseModels;
    private Long wordCount;

}
