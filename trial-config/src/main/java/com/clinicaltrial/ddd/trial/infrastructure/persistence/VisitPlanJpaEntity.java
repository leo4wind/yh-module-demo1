package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.entity.VisitPlan} domain entity.
 */
@Entity
@Table(name = "rd_project_plan_event")
public class VisitPlanJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "source_stage_id")
    private Long sourceStageId;

    @Column(name = "target_stage_id")
    private Long targetStageId;

    @Embedded
    private BaselineIntervalJpa baselineInterval;

    @Embedded
    private WindowPeriodJpa windowPeriod;

    @Column(name = "crf_component_id", length = 100)
    private String crfComponentId;

    public VisitPlanJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSourceStageId() {
        return sourceStageId;
    }

    public void setSourceStageId(Long sourceStageId) {
        this.sourceStageId = sourceStageId;
    }

    public Long getTargetStageId() {
        return targetStageId;
    }

    public void setTargetStageId(Long targetStageId) {
        this.targetStageId = targetStageId;
    }

    public BaselineIntervalJpa getBaselineInterval() {
        return baselineInterval;
    }

    public void setBaselineInterval(BaselineIntervalJpa baselineInterval) {
        this.baselineInterval = baselineInterval;
    }

    public WindowPeriodJpa getWindowPeriod() {
        return windowPeriod;
    }

    public void setWindowPeriod(WindowPeriodJpa windowPeriod) {
        this.windowPeriod = windowPeriod;
    }

    public String getCrfComponentId() {
        return crfComponentId;
    }

    public void setCrfComponentId(String crfComponentId) {
        this.crfComponentId = crfComponentId;
    }
}
