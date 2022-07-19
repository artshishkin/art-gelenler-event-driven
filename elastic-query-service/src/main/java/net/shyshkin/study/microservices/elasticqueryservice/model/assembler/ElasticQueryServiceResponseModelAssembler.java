package net.shyshkin.study.microservices.elasticqueryservice.model.assembler;

import net.shyshkin.study.microservices.elastic.model.index.impl.TwitterIndexModel;
import net.shyshkin.study.microservices.elasticqueryservice.api.ElasticDocumentController;
import net.shyshkin.study.microservices.elasticqueryservice.mapper.TwitterIndexMapper;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ElasticQueryServiceResponseModelAssembler extends RepresentationModelAssemblerSupport<TwitterIndexModel, ElasticQueryServiceResponseModel> {

    private final TwitterIndexMapper twitterIndexMapper;

    public ElasticQueryServiceResponseModelAssembler(TwitterIndexMapper twitterIndexMapper) {
        super(ElasticDocumentController.class, ElasticQueryServiceResponseModel.class);
        this.twitterIndexMapper = twitterIndexMapper;
    }

    @Override
    public ElasticQueryServiceResponseModel toModel(TwitterIndexModel entity) {
        ElasticQueryServiceResponseModel responseModel = twitterIndexMapper.toResponseModel(entity);
        Link selfLink = linkTo(methodOn(ElasticDocumentController.class).getDocumentById(entity.getId())).withSelfRel();
        Link documentsLink = linkTo(methodOn(ElasticDocumentController.class).getAllDocuments()).withRel("documents");
        responseModel.add(selfLink, documentsLink);
        return responseModel;
    }

    public List<ElasticQueryServiceResponseModel> toModels(List<TwitterIndexModel> entities) {
        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

}
