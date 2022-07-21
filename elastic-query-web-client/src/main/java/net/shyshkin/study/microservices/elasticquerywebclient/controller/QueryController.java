package net.shyshkin.study.microservices.elasticquerywebclient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientRequestModel;
import net.shyshkin.study.microservices.elasticquerywebclient.model.ElasticQueryWebClientResponseModel;
import net.shyshkin.study.microservices.elasticquerywebclient.service.ElasticWebClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

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

        List<ElasticQueryWebClientResponseModel> responseModels = elasticWebClient.getDataByText(requestModel);
        model.addAttribute("elasticQueryWebClientResponseModels", responseModels);
        model.addAttribute("searchText", searchText);
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());

        return "home";
    }


}
