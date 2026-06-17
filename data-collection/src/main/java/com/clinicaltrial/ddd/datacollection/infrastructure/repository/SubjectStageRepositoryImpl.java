package com.clinicaltrial.ddd.datacollection.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.BaselineTime;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentRef;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.FollowUpPeriod;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.datacollection.domain.repository.SubjectStageRepository;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.BaselineTimeJpa;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.FollowUpPeriodJpa;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.SubjectStageJpaEntity;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.SubjectStageSpringDataRepo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for {@link SubjectStage} aggregate.
 * Maps between domain SubjectStage aggregate and JPA entities.
 */
@Repository
@Transactional
public class SubjectStageRepositoryImpl implements SubjectStageRepository {

    private final SubjectStageSpringDataRepo springDataRepo;

    public SubjectStageRepositoryImpl(SubjectStageSpringDataRepo springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubjectStage> findById(SubjectStageId id) {
        return springDataRepo.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void save(SubjectStage subjectStage) {
        SubjectStageJpaEntity entity = toJpa(subjectStage);
        springDataRepo.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectStage> findBySubjectId(SubjectId subjectId) {
        return springDataRepo.findBySubjectsUserId(subjectId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubjectStage> findBySubjectIdAndStageId(SubjectId subjectId, StageId stageId) {
        return springDataRepo.findBySubjectsUserIdAndStageId(subjectId.getValue(), stageId.getValue())
                .map(this::toDomain);
    }

    // ========== Domain -> JPA mapping ==========

    private SubjectStageJpaEntity toJpa(SubjectStage domain) {
        SubjectStageJpaEntity entity = new SubjectStageJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setSubjectsUserId(domain.getSubjectsUserId() != null ? domain.getSubjectsUserId().getValue() : null);
        entity.setStageId(domain.getStageId() != null ? domain.getStageId().getValue() : null);
        entity.setPlanEventId(domain.getPlanEventId() != null ? domain.getPlanEventId().getValue() : null);
        entity.setStatus(domain.getStatus());
        entity.setStageStartAt(domain.getStageStartAt());
        entity.setStageEndAt(domain.getStageEndAt());
        entity.setBaselineTime(toJpaBaselineTime(domain.getBaselineTime()));
        entity.setFollowUpPeriod(toJpaFollowUpPeriod(domain.getFollowUpPeriod()));
        entity.setFollowUpStatus(domain.getFollowUpStatus());
        entity.setCompleteTime(domain.getCompleteTime());
        entity.setCompleteUserId(domain.getCompleteUserId());
        entity.setCrfAssessmentRefs(toJpaAssessmentRefs(domain.getCrfAssessmentRefs()));
        return entity;
    }

    private BaselineTimeJpa toJpaBaselineTime(BaselineTime bt) {
        if (bt == null) return null;
        return new BaselineTimeJpa(bt.getBaselineDate(), bt.getSource());
    }

    private FollowUpPeriodJpa toJpaFollowUpPeriod(FollowUpPeriod fp) {
        if (fp == null) return null;
        return new FollowUpPeriodJpa(fp.getFollowStartAt(), fp.getFollowEndAt());
    }

    private List<Long> toJpaAssessmentRefs(List<CrfAssessmentRef> refs) {
        if (refs == null) return Collections.emptyList();
        return refs.stream()
                .map(ref -> ref.getAssessmentId().getValue())
                .collect(Collectors.toList());
    }

    // ========== JPA -> Domain mapping ==========

    private SubjectStage toDomain(SubjectStageJpaEntity entity) {
        return SubjectStage.reconstruct(
                new SubjectStageId(entity.getId()),
                new SubjectId(entity.getSubjectsUserId()),
                new StageId(entity.getStageId()),
                entity.getPlanEventId() != null ? new VisitPlanId(entity.getPlanEventId()) : null,
                entity.getStatus(),
                entity.getStageStartAt(),
                entity.getStageEndAt(),
                toDomainBaselineTime(entity.getBaselineTime()),
                toDomainFollowUpPeriod(entity.getFollowUpPeriod()),
                entity.getFollowUpStatus(),
                entity.getCompleteTime(),
                entity.getCompleteUserId(),
                toDomainAssessmentRefs(entity.getCrfAssessmentRefs())
        );
    }

    private BaselineTime toDomainBaselineTime(BaselineTimeJpa jpa) {
        if (jpa == null) return null;
        return new BaselineTime(jpa.getBaselineDate(), jpa.getSource());
    }

    private FollowUpPeriod toDomainFollowUpPeriod(FollowUpPeriodJpa jpa) {
        if (jpa == null) return null;
        return new FollowUpPeriod(jpa.getFollowStartAt(), jpa.getFollowEndAt());
    }

    private List<CrfAssessmentRef> toDomainAssessmentRefs(List<Long> refIds) {
        if (refIds == null) return Collections.emptyList();
        return refIds.stream()
                .map(id -> new CrfAssessmentRef(new CrfAssessmentId(id)))
                .collect(Collectors.toList());
    }
}
