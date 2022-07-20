package net.shyshkin.study.microservices.elasticquerywebclient.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientResponseModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Controller
public class QueryController {

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

        ElasticQueryWebClientResponseModel respModel = ElasticQueryWebClientResponseModel.builder()
                .text(searchText)
                .id(UUID.randomUUID().toString())
                .createdAt(ZonedDateTime.now())
                .userId(ThreadLocalRandom.current().nextLong())
                .build();

        List<ElasticQueryWebClientResponseModel> responseModels = List.of(respModel);
        model.addAttribute("elasticQueryWebClientResponseModels", responseModels);
        model.addAttribute("searchText", searchText);
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());

        return "home";
    }


}
