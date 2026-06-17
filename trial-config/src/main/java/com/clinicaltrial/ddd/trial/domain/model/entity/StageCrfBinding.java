package com.clinicaltrial.ddd.trial.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageCrfBindingId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

import java.util.Objects;

/**
 * Entity representing a binding between a trial stage and a CRF template (阶段CRF绑定).
 * <p>
 * This binding determines which CRF (Case Report Form) template and version are
 * available for data entry in a given trial stage. A stage can have multiple CRF
 * bindings, each representing a different form to be filled out during that stage.
 * </p>
 *
 * <h3>User Input Control</h3>
 * The {@code userInputEnabled} flag controls whether the bound CRF allows direct
 * user data entry. When disabled, the CRF data may be populated automatically
 * from other sources (e.g., ETL, integrations).
 * </p>
 */
public class StageCrfBinding extends Entity<StageCrfBindingId> {

    private StageCrfBindingId id;
    private ProjectId projectId;
    private StageId stageId;
    private CrfTemplateId crfId;
    private CrfVersionId crfVersionId;
    private boolean userInputEnabled;

    /**
     * Default constructor for persistence frameworks.
     */
    protected StageCrfBinding() {
    }

    /**
     * Private constructor used by the factory method.
     */
    private StageCrfBinding(StageCrfBindingId id, ProjectId projectId, StageId stageId,
                             CrfTemplateId crfId, CrfVersionId crfVersionId,
                             boolean userInputEnabled) {
        this.id = id;
        this.projectId = projectId;
        this.stageId = stageId;
        this.crfId = crfId;
        this.crfVersionId = crfVersionId;
        this.userInputEnabled = userInputEnabled;
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new StageCrfBinding entity.
     *
     * @param id                the unique binding identity; must not be null
     * @param projectId         the project this binding belongs to; must not be null
     * @param stageId           the stage to bind the CRF to; must not be null
     * @param crfId             the CRF template to bind; must not be null
     * @param crfVersionId      the specific version of the CRF template; may be null
     *                          (defaults to latest published version)
     * @param userInputEnabled  whether direct user input is allowed for this CRF
     * @return a new StageCrfBinding instance
     * @throws IllegalArgumentException if id, projectId, stageId, or crfId is null
     */
    public static StageCrfBinding create(StageCrfBindingId id, ProjectId projectId,
                                          StageId stageId, CrfTemplateId crfId,
                                          CrfVersionId crfVersionId,
                                          boolean userInputEnabled) {
        if (id == null) {
            throw new IllegalArgumentException("StageCrfBinding id must not be null");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("StageCrfBinding projectId must not be null");
        }
        if (stageId == null) {
            throw new IllegalArgumentException("StageCrfBinding stageId must not be null");
        }
        if (crfId == null) {
            throw new IllegalArgumentException("StageCrfBinding crfId must not be null");
        }

        return new StageCrfBinding(id, projectId, stageId, crfId, crfVersionId, userInputEnabled);
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public StageCrfBindingId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public StageId getStageId() {
        return stageId;
    }

    public CrfTemplateId getCrfId() {
        return crfId;
    }

    public CrfVersionId getCrfVersionId() {
        return crfVersionId;
    }

    public boolean isUserInputEnabled() {
        return userInputEnabled;
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(StageCrfBindingId id) {
        this.id = id;
    }

    public void setProjectId(ProjectId projectId) {
        this.projectId = projectId;
    }

    public void setStageId(StageId stageId) {
        this.stageId = stageId;
    }

    public void setCrfId(CrfTemplateId crfId) {
        this.crfId = crfId;
    }

    public void setCrfVersionId(CrfVersionId crfVersionId) {
        this.crfVersionId = crfVersionId;
    }

    public void setUserInputEnabled(boolean userInputEnabled) {
        this.userInputEnabled = userInputEnabled;
    }

    @Override
    public String toString() {
        return "StageCrfBinding{id=" + id + ", stage=" + stageId + ", crf=" + crfId + '}';
    }
}
