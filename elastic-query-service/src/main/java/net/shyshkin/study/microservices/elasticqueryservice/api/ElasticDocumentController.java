package net.shyshkin.study.microservices.elasticqueryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticqueryservice.business.ElasticQueryService;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModelV2;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
@RequiredArgsConstructor
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;

    @GetMapping
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {

        List<ElasticQueryServiceResponseModel> docs = elasticQueryService.getAllDocuments();
        log.debug("Elasticsearch returned {} documents", docs.size());
        return docs;
    }

    @GetMapping("/{id}")
    public ElasticQueryServiceResponseModel getDocumentById(@PathVariable String id) {
        ElasticQueryServiceResponseModel doc = elasticQueryService.getDocumentById(id);
        log.debug("Elasticsearch returned document with id {}", id);
        return doc;
    }

    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public ElasticQueryServiceResponseModelV2 getDocumentByIdV2(@PathVariable String id) {
        ElasticQueryServiceResponseModel doc = elasticQueryService.getDocumentById(id);
        log.debug("Elasticsearch returned document with id {}", id);
        return mapToV2(doc);
    }

    ElasticQueryServiceResponseModelV2 mapToV2(ElasticQueryServiceResponseModel responseModel) {
        ElasticQueryServiceResponseModelV2 v2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.valueOf(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .text2("Version 2 text: " + responseModel.getText().toUpperCase())
                .build();
        v2.add(responseModel.getLinks());
        return v2;
    }

    @PostMapping("/get-document-by-text")
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(@Valid @RequestBody ElasticQueryServiceRequestModel requestModel) {
        List<ElasticQueryServiceResponseModel> docs = elasticQueryService.getDocumentsByText(requestModel);
        log.debug("Elasticsearch returned {} documents for text `{}`", docs.size(), requestModel.getText());
        return docs;
    }

}
