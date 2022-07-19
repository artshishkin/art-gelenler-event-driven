package net.shyshkin.study.microservices.elasticqueryservice.model;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticQueryServiceResponseModelV2 extends RepresentationModel<ElasticQueryServiceResponseModelV2> {

    private Long id;
    private Long userId;
    private String text;
    private String text2;

}
