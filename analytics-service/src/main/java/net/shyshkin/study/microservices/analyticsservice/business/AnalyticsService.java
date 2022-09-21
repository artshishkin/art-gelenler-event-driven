package net.shyshkin.study.microservices.analyticsservice.business;

import net.shyshkin.study.microservices.analyticsservice.model.AnalyticsResponseModel;

import java.util.Optional;

public interface AnalyticsService {

    Optional<AnalyticsResponseModel> getWordAnalytics(String word);

}
