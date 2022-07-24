package net.shyshkin.study.microservices.reactiveelasticqueryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceRequestModel;
import net.shyshkin.study.microservices.elasticqueryservicecommon.model.ElasticQueryServiceResponseModel;
import net.shyshkin.study.microservices.reactiveelasticqueryservice.business.ElasticQueryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping(value = "/documents", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
@RequiredArgsConstructor
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;

    @GetMapping
    public Flux<ElasticQueryServiceResponseModel> getAllDocuments() {
        AtomicInteger counter = new AtomicInteger(0);
        return elasticQueryService.getAllDocuments()
                .doFirst(() -> log.debug("Requesting all the documents"))
                .doOnNext(model -> counter.incrementAndGet())
                .doFinally(signalType -> log.debug("Elasticsearch returned {} documents", counter.get()));
    }

    @GetMapping("/{id}")
    public Mono<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable String id) {
        return elasticQueryService.getDocumentById(id)
                .doOnSuccess(responseModel -> log.debug("Elasticsearch returned document with id {}", id));
    }

    @PostMapping("/get-document-by-text")
    public Flux<ElasticQueryServiceResponseModel> getDocumentsByText(@Valid @RequestBody ElasticQueryServiceRequestModel requestModel) {
        AtomicInteger counter = new AtomicInteger(0);
        return elasticQueryService.getDocumentsByText(requestModel)
                .doFirst(() -> log.debug("Requesting the documents for text `{}`", requestModel.getText()))
                .doOnNext(respModel -> counter.incrementAndGet())
                .doFinally(ignored -> log.debug("Elasticsearch returned {} documents for text `{}`",
                        counter.get(), requestModel.getText())
                );
    }
}
