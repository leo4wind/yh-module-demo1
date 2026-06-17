package com.clinicaltrial.ddd.query.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SnapshotValue;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;
import com.clinicaltrial.ddd.query.infrastructure.persistence.QueryJpaEntity;
import com.clinicaltrial.ddd.query.infrastructure.persistence.QuerySpringDataRepo;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for Query aggregate.
 * Maps between domain Query aggregate and JPA entities.
 */
@Repository
@Transactional
public class QueryRepositoryImpl implements QueryRepository {

    private final QuerySpringDataRepo springDataRepo;

    public QueryRepositoryImpl(QuerySpringDataRepo springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Query> findById(QueryId queryId) {
        return springDataRepo.findById(queryId.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public Query save(Query query) {
        QueryJpaEntity entity = toJpa(query);
        QueryJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Query> findByAssessmentId(CrfAssessmentId assessmentId) {
        return springDataRepo.findByAssessmentId(assessmentId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int countOpenQueriesByAssessmentId(CrfAssessmentId assessmentId) {
        Long assessmentIdValue = assessmentId.getValue();
        int openCount = springDataRepo.countByAssessmentIdAndStatus(assessmentIdValue, QueryStatus.OPEN);
        int respondedCount = springDataRepo.countByAssessmentIdAndStatus(assessmentIdValue, QueryStatus.RESPONDED);
        return openCount + respondedCount;
    }

    // ========== Domain -> JPA mapping ==========

    private QueryJpaEntity toJpa(Query domain) {
        QueryJpaEntity entity = new QueryJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setAssessmentId(domain.getAssessmentId() != null ? domain.getAssessmentId().getValue() : null);

        if (domain.getFieldIdentifier() != null) {
            entity.setFieldCode(domain.getFieldIdentifier().getFieldCode());
            entity.setSubTableId(domain.getFieldIdentifier().getSubTableId());
            entity.setFieldType(domain.getFieldIdentifier().getFieldType());
        }

        entity.setStatus(domain.getStatus());
        entity.setType(domain.getType());
        entity.setQuestion(domain.getQuestion());
        entity.setResponse(domain.getResponse());
        entity.setUpdateType(domain.getUpdateType());
        entity.setCreateUserId(domain.getCreateUserId());
        entity.setUpdateUserId(domain.getUpdateUserId());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());

        if (domain.getOriginalValue() != null) {
            entity.setOriginalFieldCode(domain.getOriginalValue().getFieldCode());
            entity.setOriginalFieldLabel(domain.getOriginalValue().getFieldLabel());
            entity.setOriginalFieldValue(domain.getOriginalValue().getFieldValue());
            entity.setOriginalFieldValueText(domain.getOriginalValue().getFieldValueText());
            entity.setOriginalSnapshotAt(domain.getOriginalValue().getSnapshotAt());
        }

        if (domain.getCurrentValue() != null) {
            entity.setCurrentFieldCode(domain.getCurrentValue().getFieldCode());
            entity.setCurrentFieldLabel(domain.getCurrentValue().getFieldLabel());
            entity.setCurrentFieldValue(domain.getCurrentValue().getFieldValue());
            entity.setCurrentFieldValueText(domain.getCurrentValue().getFieldValueText());
            entity.setCurrentSnapshotAt(domain.getCurrentValue().getSnapshotAt());
        }

        return entity;
    }

    // ========== JPA -> Domain mapping ==========

    private Query toDomain(QueryJpaEntity entity) {
        QueryFieldIdentifier fieldIdentifier = new QueryFieldIdentifier(
                entity.getFieldCode(),
                entity.getSubTableId(),
                entity.getFieldType()
        );

        SnapshotValue originalValue = null;
        if (entity.getOriginalFieldCode() != null) {
            originalValue = new SnapshotValue(
                    entity.getOriginalFieldCode(),
                    entity.getOriginalFieldLabel(),
                    entity.getOriginalFieldValue(),
                    entity.getOriginalFieldValueText(),
                    entity.getOriginalSnapshotAt()
            );
        }

        SnapshotValue currentValue = null;
        if (entity.getCurrentFieldCode() != null) {
            currentValue = new SnapshotValue(
                    entity.getCurrentFieldCode(),
                    entity.getCurrentFieldLabel(),
                    entity.getCurrentFieldValue(),
                    entity.getCurrentFieldValueText(),
                    entity.getCurrentSnapshotAt()
            );
        }

        return Query.reconstruct(
                new QueryId(entity.getId()),
                new CrfAssessmentId(entity.getAssessmentId()),
                fieldIdentifier,
                entity.getStatus(),
                entity.getType(),
                entity.getQuestion(),
                entity.getResponse(),
                entity.getUpdateType(),
                entity.getCreateUserId(),
                entity.getUpdateUserId(),
                entity.getCreateTime(),
                entity.getUpdateTime(),
                originalValue,
                currentValue
        );
    }
}
