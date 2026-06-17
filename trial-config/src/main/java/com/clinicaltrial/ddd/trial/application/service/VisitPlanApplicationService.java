package com.clinicaltrial.ddd.trial.application.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.application.command.ConfigureVisitPlanCommand;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.BaselineInterval;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.WindowPeriod;
import com.clinicaltrial.ddd.trial.domain.repository.ProjectRepository;
import com.clinicaltrial.ddd.trial.domain.service.VisitPlanDomainService;

/**
 * Application service for Visit Plan management use cases.
 * <p>
 * Orchestrates the configuration of visit plans within a clinical trial
 * project. This service coordinates between the Project aggregate and the
 * {@link VisitPlanDomainService} for cross-entity validation rules such
 * as cycle detection in the visit plan directed graph.
 * </p>
 *
 * <h3>Use Cases</h3>
 * <ul>
 *   <li>{@link #configureVisitPlan(ConfigureVisitPlanCommand)} — Configure a
 *       new visit plan linking source and target stages</li>
 * </ul>
 */
@Service
public class VisitPlanApplicationService {

    private final ProjectRepository projectRepository;
    private final VisitPlanDomainService visitPlanDomainService;
    private final EventBus eventBus;

    /**
     * Constructs the application service with its required dependencies.
     *
     * @param projectRepository    the repository for Project aggregates
     * @param visitPlanDomainService the domain service for visit plan validation
     * @param eventBus             the infrastructure event bus
     */
    public VisitPlanApplicationService(ProjectRepository projectRepository,
                                        VisitPlanDomainService visitPlanDomainService,
                                        EventBus eventBus) {
        this.projectRepository = projectRepository;
        this.visitPlanDomainService = visitPlanDomainService;
        this.eventBus = eventBus;
    }

    /**
     * Configures a new visit plan for the project.
     * <p>
     * Loads the project, validates the timeline (no self-loops, no cycles),
     * delegates to {@link Project#configureVisitPlan(VisitPlanId, String,
     * com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId,
     * com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId,
     * BaselineInterval, WindowPeriod, String)}, persists, and publishes events.
     * </p>
     *
     * @param command the configure-visit-plan command
     * @return the updated project
     * @throws IllegalArgumentException         if command is null
     * @throws BusinessRuleViolationException    if business rules are violated
     */
    public Project configureVisitPlan(ConfigureVisitPlanCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        // Load the project aggregate
        Project project = projectRepository.getById(command.getProjectId());

        // Validate timeline using domain service
        visitPlanDomainService.validateDirectedTimeline(
                project, command.getSourceStageId(), command.getTargetStageId());

        // Build value objects
        BaselineInterval baselineInterval = null;
        if (command.getBaselineInterval() != null && command.getBaselineIntervalUnit() != null) {
            baselineInterval = new BaselineInterval(
                    command.getBaselineInterval(), command.getBaselineIntervalUnit());
        }

        WindowPeriod windowPeriod = null;
        if (command.getWindowBeforeDays() != null || command.getWindowAfterDays() != null) {
            windowPeriod = new WindowPeriod(
                    command.getWindowBeforeDays() != null ? command.getWindowBeforeDays() : 0L,
                    command.getWindowAfterDays() != null ? command.getWindowAfterDays() : 0L);
        }

        // Delegate to the aggregate
        VisitPlanId visitPlanId = new VisitPlanId(null); // ID will be generated
        project.configureVisitPlan(visitPlanId, command.getName(),
                command.getSourceStageId(), command.getTargetStageId(),
                baselineInterval, windowPeriod, command.getCrfComponentId());

        // Validate window consistency
        // (Would load the created visit plan and validate, but the ID is generated
        // at persistence time; validation can be done post-save or in a domain
        // event handler.)

        // Persist
        Project savedProject = projectRepository.save(project);

        // Publish events
        eventBus.publishAll(savedProject);

        return savedProject;
    }
}
