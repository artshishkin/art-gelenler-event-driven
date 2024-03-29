package net.shyshkin.study.microservices.elasticqueryservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceWordCountResponseModel {

    private String word;
    private Long wordCount;

}
