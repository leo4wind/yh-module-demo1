package com.clinicaltrial.ddd.datacollection.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.AssessmentScore;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfFieldValueId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.AssessmentScoreJpa;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.CompletenessJpa;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.CrfAssessmentJpaEntity;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.CrfAssessmentSpringDataRepo;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.CrfFieldValueJpaEntity;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for {@link CrfAssessment} aggregate.
 * Maps between domain CrfAssessment aggregate and JPA entities.
 */
@Repository
@Transactional
public class CrfAssessmentRepositoryImpl implements CrfAssessmentRepository {

    private final CrfAssessmentSpringDataRepo springDataRepo;

    public CrfAssessmentRepositoryImpl(CrfAssessmentSpringDataRepo springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CrfAssessment> findById(CrfAssessmentId id) {
        return springDataRepo.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public CrfAssessment getById(CrfAssessmentId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("CrfAssessment", id));
    }

    @Override
    @Transactional
    public void save(CrfAssessment assessment) {
        CrfAssessmentJpaEntity entity = toJpa(assessment);
        springDataRepo.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrfAssessment> findBySubjectsStageId(SubjectStageId subjectStageId) {
        return springDataRepo.findBySubjectsStageId(subjectStageId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrfAssessment> findBySubjectId(SubjectId subjectId) {
        return springDataRepo.findBySubjectsUserId(subjectId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ========== Domain -> JPA mapping ==========

    private CrfAssessmentJpaEntity toJpa(CrfAssessment domain) {
        CrfAssessmentJpaEntity entity = new CrfAssessmentJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setSubjectsUserId(domain.getSubjectsUserId() != null ? domain.getSubjectsUserId().getValue() : null);
        entity.setCrfId(domain.getCrfId() != null ? domain.getCrfId().getValue() : null);
        entity.setCrfVersionId(domain.getCrfVersionId() != null ? domain.getCrfVersionId().getValue() : null);
        entity.setSubjectsStageId(domain.getSubjectsStageId() != null ? domain.getSubjectsStageId().getValue() : null);
        entity.setStatus(domain.getStatus());
        entity.setCompleteness(toJpaCompleteness(domain.getCompleteness()));
        entity.setAdverseEvent(domain.isAdverseEvent());
        entity.setAssessmentScore(toJpaAssessmentScore(domain.getAssessmentScore()));
        entity.setFieldValues(toJpaFieldValues(domain.getFieldValues(), entity.getId()));
        return entity;
    }

    private CompletenessJpa toJpaCompleteness(Completeness c) {
        if (c == null) return null;
        return new CompletenessJpa(c.getPercentage(), c.getFilledCount(), c.getTotalCount());
    }

    private AssessmentScoreJpa toJpaAssessmentScore(AssessmentScore s) {
        if (s == null) return null;
        return new AssessmentScoreJpa(s.getScore(), s.getResult());
    }

    private List<CrfFieldValueJpaEntity> toJpaFieldValues(List<CrfFieldValue> fieldValues, Long assessmentId) {
        if (fieldValues == null) return Collections.emptyList();
        return fieldValues.stream()
                .map(fv -> toJpaFieldValue(fv, assessmentId))
                .collect(Collectors.toList());
    }

    private CrfFieldValueJpaEntity toJpaFieldValue(CrfFieldValue fv, Long assessmentId) {
        CrfFieldValueJpaEntity entity = new CrfFieldValueJpaEntity();
        entity.setId(fv.getId() != null ? fv.getId().getValue() : null);
        entity.setAssessmentId(assessmentId);
        entity.setFieldCode(fv.getFieldCode());
        entity.setFieldLabel(fv.getFieldLabel());
        entity.setFieldValue(fv.getFieldValue());
        entity.setFieldValueText(fv.getFieldValueText());
        entity.setDataUnit(fv.getDataUnit());
        entity.setFieldType(fv.getFieldType());
        entity.setSubTableId(fv.getSubTableId());
        entity.setSortNumber(fv.getSortNumber());
        return entity;
    }

    // ========== JPA -> Domain mapping ==========

    private CrfAssessment toDomain(CrfAssessmentJpaEntity entity) {
        return CrfAssessment.reconstruct(
                new CrfAssessmentId(entity.getId()),
                entity.getSubjectsUserId() != null ? new SubjectId(entity.getSubjectsUserId()) : null,
                entity.getCrfId() != null ? new CrfTemplateId(entity.getCrfId()) : null,
                entity.getCrfVersionId() != null ? new CrfVersionId(entity.getCrfVersionId()) : null,
                entity.getSubjectsStageId() != null ? new SubjectStageId(entity.getSubjectsStageId()) : null,
                entity.getStatus(),
                toDomainCompleteness(entity.getCompleteness()),
                entity.isAdverseEvent(),
                toDomainAssessmentScore(entity.getAssessmentScore()),
                toDomainFieldValues(entity.getFieldValues(), entity.getId())
        );
    }

    private Completeness toDomainCompleteness(CompletenessJpa jpa) {
        if (jpa == null) return Completeness.zero();
        return new Completeness(jpa.getPercentage(), jpa.getFilledCount(), jpa.getTotalCount());
    }

    private AssessmentScore toDomainAssessmentScore(AssessmentScoreJpa jpa) {
        if (jpa == null) return null;
        return new AssessmentScore(jpa.getScore(), jpa.getResult());
    }

    private List<CrfFieldValue> toDomainFieldValues(List<CrfFieldValueJpaEntity> entities, Long assessmentId) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(e -> toDomainFieldValue(e, assessmentId))
                .collect(Collectors.toList());
    }

    private CrfFieldValue toDomainFieldValue(CrfFieldValueJpaEntity entity, Long assessmentId) {
        return new CrfFieldValue(
                new CrfFieldValueId(entity.getId()),
                new CrfAssessmentId(assessmentId),
                entity.getFieldCode(),
                entity.getFieldLabel(),
                entity.getFieldValue(),
                entity.getFieldValueText(),
                entity.getDataUnit(),
                entity.getFieldType(),
                entity.getSubTableId(),
                entity.getSortNumber()
        );
    }
}
