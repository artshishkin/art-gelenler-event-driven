package net.shyshkin.study.microservices.analyticsservice.dataaccess.repository.impl;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.microservices.analyticsservice.dataaccess.entity.BaseEntity;
import net.shyshkin.study.microservices.analyticsservice.dataaccess.repository.AnalyticsCustomRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;

@Slf4j
@Repository
public class AnalyticsRepositoryImpl<T extends BaseEntity<PK>, PK> implements AnalyticsCustomRepository<T, PK> {

    @PersistenceContext
    protected EntityManager em;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:20}")
    protected int batchSize;

    @Override
    @Transactional
    public <S extends T> PK persist(S entity) {
        em.persist(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public <S extends T> void batchPersist(Collection<S> entities) {
        if (entities.isEmpty()) {
            log.debug("No entity found to insert");
            return;
        }
        int batchCnt = 0;
        for (S entity : entities) {
            log.trace("Persisting entity with id {}", entity.getId());
            em.persist(entity);
            batchCnt++;
            if (batchCnt % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
        if (batchCnt % batchSize == 0) {
            em.flush();
            em.clear();
        }
    }

    @Override
    @Transactional
    public <S extends T> S merge(S entity) {
        return em.merge(entity);
    }

    @Override
    public <S extends T> void batchMerge(Collection<S> entities) {
        if (entities.isEmpty()) {
            log.debug("No entity found to merge");
            return;
        }
        int batchCnt = 0;
        for (S entity : entities) {
            log.trace("Merging entity with id {}", entity.getId());
            em.merge(entity);
            batchCnt++;
            if (batchCnt % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
        if (batchCnt % batchSize == 0) {
            em.flush();
            em.clear();
        }
    }

    @Override
    public void clear() {
        em.clear();
    }
}
