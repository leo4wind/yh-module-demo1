package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.entity.StageCrfBinding} domain entity.
 */
@Entity
@Table(name = "rd_project_stage_crf_binding")
public class StageCrfBindingJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "stage_id", nullable = false)
    private Long stageId;

    @Column(name = "crf_id", nullable = false)
    private Long crfId;

    @Column(name = "crf_version_id")
    private Long crfVersionId;

    @Column(name = "user_input_enabled")
    private boolean userInputEnabled;

    public StageCrfBindingJpaEntity() {
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

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
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

    public boolean isUserInputEnabled() {
        return userInputEnabled;
    }

    public void setUserInputEnabled(boolean userInputEnabled) {
        this.userInputEnabled = userInputEnabled;
    }
}
