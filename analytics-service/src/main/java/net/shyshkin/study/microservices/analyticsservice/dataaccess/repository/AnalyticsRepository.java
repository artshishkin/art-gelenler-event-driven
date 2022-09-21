package net.shyshkin.study.microservices.analyticsservice.dataaccess.repository;

import net.shyshkin.study.microservices.analyticsservice.dataaccess.entity.AnalyticsEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AnalyticsRepository extends JpaRepository<AnalyticsEntity, UUID>, AnalyticsCustomRepository<AnalyticsEntity, UUID> {

    List<AnalyticsEntity> getAnalyticsEntitiesByWordOrderByRecordDateDesc(String word, Pageable pageable);

    @Query("select e from AnalyticsEntity e where e.word=:word order by e.recordDate desc")
    List<AnalyticsEntity> getAnalyticsEntitiesByWordCustomQuery(@Param("word") String word, Pageable pageable);

}
