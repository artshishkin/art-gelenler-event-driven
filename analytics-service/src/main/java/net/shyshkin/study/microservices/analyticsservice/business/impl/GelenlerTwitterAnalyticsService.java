package net.shyshkin.study.microservices.analyticsservice.business.impl;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.analyticsservice.business.AnalyticsService;
import net.shyshkin.study.microservices.analyticsservice.dataaccess.repository.AnalyticsRepository;
import net.shyshkin.study.microservices.analyticsservice.mapper.AnalyticsMapper;
import net.shyshkin.study.microservices.analyticsservice.model.AnalyticsResponseModel;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("gelenler")
public class GelenlerTwitterAnalyticsService implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final AnalyticsMapper mapper;

    @Override
    public Optional<AnalyticsResponseModel> getWordAnalytics(String word) {
        var latestResult = analyticsRepository
                .getAnalyticsEntitiesByWordCustomQuery(word.toLowerCase(), PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        return mapper.toResponseModel(latestResult);
    }
}
