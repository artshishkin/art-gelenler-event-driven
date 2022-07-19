package net.shyshkin.study.microservices.elasticqueryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticqueryservice.business.ElasticQueryService;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservice.model.ElasticQueryServiceResponseModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;

    @GetMapping("/v1")
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {

        List<ElasticQueryServiceResponseModel> docs = elasticQueryService.getAllDocuments();
        log.debug("Elasticsearch returned {} documents", docs.size());
        return docs;
    }

    @GetMapping("/v1/{id}")
    public ElasticQueryServiceResponseModel getDocumentById(@PathVariable String id) {
        ElasticQueryServiceResponseModel doc = elasticQueryService.getDocumentById(id);
        log.debug("Elasticsearch returned document with id {}", id);
        return doc;
    }

    @PostMapping("/v1/get-document-by-text")
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(@Valid @RequestBody ElasticQueryServiceRequestModel requestModel) {
        List<ElasticQueryServiceResponseModel> docs = elasticQueryService.getDocumentsByText(requestModel);
        log.debug("Elasticsearch returned {} documents for text `{}`", docs.size(), requestModel.getText());
        return docs;
    }

}
