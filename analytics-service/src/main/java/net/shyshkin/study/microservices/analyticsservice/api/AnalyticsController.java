package net.shyshkin.study.microservices.analyticsservice.api;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.analyticsservice.business.AnalyticsService;
import net.shyshkin.study.microservices.analyticsservice.model.AnalyticsResponseModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(value = "/", produces = "application/vnd.api.v1+json")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/get-word-count-by-word/{word}")
    public AnalyticsResponseModel getAnalyticsForWord(@PathVariable @NotEmpty String word) {
        return analyticsService
                .getWordAnalytics(word)
                .orElse(AnalyticsResponseModel.builder().build());
    }

}
