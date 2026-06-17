package com.clinicaltrial.ddd.trial.application.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.application.command.BindCrfToStageCommand;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageCrfBindingId;
import com.clinicaltrial.ddd.trial.domain.repository.ProjectRepository;

import java.util.UUID;

/**
 * Application service for CRF-to-Stage binding use cases.
 * <p>
 * Orchestrates the binding of CRF templates to trial stages. This service
 * delegates all business logic to the Project aggregate.
 * </p>
 *
 * <h3>Use Cases</h3>
 * <ul>
 *   <li>{@link #bindCrf(BindCrfToStageCommand)} — Bind a CRF template to a stage</li>
 * </ul>
 */
@Service
public class CrfBindingApplicationService {

    private final ProjectRepository projectRepository;
    private final EventBus eventBus;

    /**
     * Constructs the application service with its required dependencies.
     *
     * @param projectRepository the repository for Project aggregates
     * @param eventBus          the infrastructure event bus
     */
    public CrfBindingApplicationService(ProjectRepository projectRepository, EventBus eventBus) {
        this.projectRepository = projectRepository;
        this.eventBus = eventBus;
    }

    /**
     * Binds a CRF template to a trial stage.
     * <p>
     * Loads the project, delegates to {@link Project#bindCrf(StageCrfBindingId,
     * com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId,
     * com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId,
     * com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId, boolean)},
     * persists, and publishes events.
     * </p>
     *
     * @param command the bind-CRF command
     * @return the updated project
     * @throws IllegalArgumentException         if command is null
     * @throws BusinessRuleViolationException    if business rules are violated
     */
    public Project bindCrf(BindCrfToStageCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        // Load the project aggregate
        Project project = projectRepository.getById(command.getProjectId());

        // Delegate to the aggregate
        StageCrfBindingId bindingId = new StageCrfBindingId(generateNumericId());
        project.bindCrf(bindingId, command.getStageId(), command.getCrfId(),
                command.getCrfVersionId(), command.isUserInputEnabled());

        // Persist
        Project savedProject = projectRepository.save(project);

        // Publish events
        eventBus.publishAll(savedProject);

        return savedProject;
    }

    private Long generateNumericId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
