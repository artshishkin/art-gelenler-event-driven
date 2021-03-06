package net.shyshkin.study.microservices.reactiveelasticquerywebclient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclientcommon.model.ElasticQueryWebClientResponseModel;
import net.shyshkin.study.microservices.reactiveelasticquerywebclient.service.ElasticWebClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QueryController {

    private final ElasticWebClient elasticWebClient;

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("error")
    public String error() {
        return "error";
    }

    @GetMapping("home")
    public String home(Model model) {
        ElasticQueryWebClientRequestModel requestModel = ElasticQueryWebClientRequestModel.builder()
                .build();
        model.addAttribute("elasticQueryWebClientRequestModel", requestModel);
        return "home";
    }

    @PostMapping("query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel, Model model) {
        String searchText = requestModel.getText();
        log.debug("Searching for {}", searchText);

        Flux<ElasticQueryWebClientResponseModel> responseModels = elasticWebClient.getDataByText(requestModel);

        IReactiveDataDriverContextVariable reactiveData =
                new ReactiveDataDriverContextVariable(responseModels.log(), 1);

        model.addAttribute("elasticQueryWebClientResponseModels", reactiveData);
        model.addAttribute("searchText", searchText);
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());

        return "home";
    }


}
