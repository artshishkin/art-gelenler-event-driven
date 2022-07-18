package net.shyshkin.study.microservices.elasticqueryservice.api;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/documents")
public class ElasticDocumentController {

    @GetMapping
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {

        List<ElasticQueryServiceResponseModel> docs = List.of();
        log.debug("Elasticsearch returned {} documents", docs.size());
        return docs;
    }

    @GetMapping("/{id}")
    public ElasticQueryServiceResponseModel getDocumentById(@PathVariable String id) {
        ElasticQueryServiceResponseModel doc = ElasticQueryServiceResponseModel.builder()
                .id(id)
                .build();
        log.debug("Elasticsearch returned document with id {}", id);
        return doc;
    }

    @PostMapping("/get-document-by-text")
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(@RequestBody ElasticQueryServiceRequestModel requestModel) {
        List<ElasticQueryServiceResponseModel> docs = List.of(
                ElasticQueryServiceResponseModel.builder()
                        .text(requestModel.getText())
                        .build()
        );
        log.debug("Elasticsearch returned {} documents for text `{}`", docs.size(), requestModel.getText());
        return docs;
    }

}
