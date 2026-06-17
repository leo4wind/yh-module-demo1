package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.MonitoringStatus;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment} aggregate root.
 */
@Entity
@Table(name = "rd_crf_assessment")
public class CrfAssessmentJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subjects_user_id", nullable = false)
    private Long subjectsUserId;

    @Column(name = "crf_id", nullable = false)
    private Long crfId;

    @Column(name = "crf_version_id")
    private Long crfVersionId;

    @Column(name = "subjects_stage_id", nullable = false)
    private Long subjectsStageId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private MonitoringStatus status;

    @Embedded
    private CompletenessJpa completeness;

    @Column(name = "adverse_event")
    private boolean adverseEvent;

    @Embedded
    private AssessmentScoreJpa assessmentScore;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assessment_id")
    private List<CrfFieldValueJpaEntity> fieldValues = new ArrayList<>();

    public CrfAssessmentJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectsUserId() {
        return subjectsUserId;
    }

    public void setSubjectsUserId(Long subjectsUserId) {
        this.subjectsUserId = subjectsUserId;
    }

    public Long getCrfId() {
        return crfId;
    }

    public void setCrfId(Long crfId) {
        this.crfId = crfId;
    }

    public Long getCrfVersionId() {
        return crfVersionId;
    }

    public void setCrfVersionId(Long crfVersionId) {
        this.crfVersionId = crfVersionId;
    }

    public Long getSubjectsStageId() {
        return subjectsStageId;
    }

    public void setSubjectsStageId(Long subjectsStageId) {
        this.subjectsStageId = subjectsStageId;
    }

    public MonitoringStatus getStatus() {
        return status;
    }

    public void setStatus(MonitoringStatus status) {
        this.status = status;
    }

    public CompletenessJpa getCompleteness() {
        return completeness;
    }

    public void setCompleteness(CompletenessJpa completeness) {
        this.completeness = completeness;
    }

    public boolean isAdverseEvent() {
        return adverseEvent;
    }

    public void setAdverseEvent(boolean adverseEvent) {
        this.adverseEvent = adverseEvent;
    }

    public AssessmentScoreJpa getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(AssessmentScoreJpa assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public List<CrfFieldValueJpaEntity> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<CrfFieldValueJpaEntity> fieldValues) {
        this.fieldValues = fieldValues;
    }
}
