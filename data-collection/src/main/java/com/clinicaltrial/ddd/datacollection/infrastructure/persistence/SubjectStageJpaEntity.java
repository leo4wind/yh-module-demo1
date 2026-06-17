package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageStatus;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage} aggregate root.
 */
@Entity
@Table(name = "rd_subject_stage")
public class SubjectStageJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subjects_user_id", nullable = false)
    private Long subjectsUserId;

    @Column(name = "stage_id", nullable = false)
    private Long stageId;

    @Column(name = "plan_event_id")
    private Long planEventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubjectStageStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "stage_start_at")
    private Date stageStartAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "stage_end_at")
    private Date stageEndAt;

    @Embedded
    private BaselineTimeJpa baselineTime;

    @Embedded
    private FollowUpPeriodJpa followUpPeriod;

    @Column(name = "follow_up_status", length = 50)
    private String followUpStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "complete_time")
    private Date completeTime;

    @Column(name = "complete_user_id")
    private Long completeUserId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "rd_subject_stage_assessment_ref", joinColumns = @JoinColumn(name = "subject_stage_id"))
    @Column(name = "assessment_id")
    private List<Long> crfAssessmentRefs = new ArrayList<>();

    public SubjectStageJpaEntity() {
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

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public Long getPlanEventId() {
        return planEventId;
    }

    public void setPlanEventId(Long planEventId) {
        this.planEventId = planEventId;
    }

    public SubjectStageStatus getStatus() {
        return status;
    }

    public void setStatus(SubjectStageStatus status) {
        this.status = status;
    }

    public Date getStageStartAt() {
        return stageStartAt;
    }

    public void setStageStartAt(Date stageStartAt) {
        this.stageStartAt = stageStartAt;
    }

    public Date getStageEndAt() {
        return stageEndAt;
    }

    public void setStageEndAt(Date stageEndAt) {
        this.stageEndAt = stageEndAt;
    }

    public BaselineTimeJpa getBaselineTime() {
        return baselineTime;
    }

    public void setBaselineTime(BaselineTimeJpa baselineTime) {
        this.baselineTime = baselineTime;
    }

    public FollowUpPeriodJpa getFollowUpPeriod() {
        return followUpPeriod;
    }

    public void setFollowUpPeriod(FollowUpPeriodJpa followUpPeriod) {
        this.followUpPeriod = followUpPeriod;
    }

    public String getFollowUpStatus() {
        return followUpStatus;
    }

    public void setFollowUpStatus(String followUpStatus) {
        this.followUpStatus = followUpStatus;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Long getCompleteUserId() {
        return completeUserId;
    }

    public void setCompleteUserId(Long completeUserId) {
        this.completeUserId = completeUserId;
    }

    public List<Long> getCrfAssessmentRefs() {
        return crfAssessmentRefs;
    }

    public void setCrfAssessmentRefs(List<Long> crfAssessmentRefs) {
        this.crfAssessmentRefs = crfAssessmentRefs;
    }
}
