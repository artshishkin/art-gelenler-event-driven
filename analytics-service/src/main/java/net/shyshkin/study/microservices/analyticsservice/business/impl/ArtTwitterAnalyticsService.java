package net.shyshkin.study.microservices.analyticsservice.business.impl;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.microservices.analyticsservice.business.AnalyticsService;
import net.shyshkin.study.microservices.analyticsservice.dataaccess.repository.AnalyticsRepository;
import net.shyshkin.study.microservices.analyticsservice.mapper.AnalyticsMapper;
import net.shyshkin.study.microservices.analyticsservice.model.AnalyticsResponseModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("!gelenler")
public class ArtTwitterAnalyticsService implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final AnalyticsMapper mapper;

    @Override
    public Optional<AnalyticsResponseModel> getWordAnalytics(String word) {
        var latestResult = analyticsRepository
                .findFirstByWordOrderByRecordDateDesc(word.toLowerCase())
                .orElse(null);
        return mapper.toResponseModel(latestResult);
    }
}
