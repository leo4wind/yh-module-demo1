package com.clinicaltrial.ddd.trial.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.domain.repository.ProjectRepository;

import java.util.Date;

/**
 * Domain service for project-level business rules and validations.
 * <p>
 * Domain services encapsulate domain logic that does not naturally fit within
 * a single entity or aggregate. This service handles cross-aggregate
 * validation rules for trial projects.
 * </p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Validating project date consistency (start date before end date)</li>
 *   <li>Checking prefix uniqueness across all projects</li>
 * </ul>
 */
@Service
public class ProjectDomainService {

    private final ProjectRepository projectRepository;

    /**
     * Creates a new ProjectDomainService.
     *
     * @param projectRepository the repository for querying project data; must not be null
     */
    public ProjectDomainService(ProjectRepository projectRepository) {
        if (projectRepository == null) {
            throw new IllegalArgumentException("projectRepository must not be null");
        }
        this.projectRepository = projectRepository;
    }

    /**
     * Validates that the project dates are consistent.
     * <p>
     * The start date must be before (or on) the end date. If either date is null,
     * validation is skipped.
     * </p>
     *
     * @param startAt the project start date; may be null
     * @param endAt   the project end date; may be null
     * @throws BusinessRuleViolationException if end date is before start date
     */
    public void validateProjectDates(Date startAt, Date endAt) {
        if (startAt != null && endAt != null) {
            if (endAt.before(startAt)) {
                throw new BusinessRuleViolationException("INVALID_PROJECT_DATES",
                        "Project end date [" + endAt + "] must not be before start date ["
                                + startAt + "]");
            }
        }
    }

    /**
     * Validates that the given project prefix is unique across all projects.
     * <p>
     * The project prefix is used as a short identifier code and must not
     * conflict with any existing project.
     * </p>
     *
     * @param prefix the project prefix to check; must not be null
     * @throws BusinessRuleViolationException if a project with the same prefix already exists
     * @throws IllegalArgumentException        if prefix is null
     */
    public void validatePrefixUniqueness(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Prefix must not be null or empty");
        }

        long count = projectRepository.countByPrefix(prefix.trim());
        if (count > 0) {
            throw new BusinessRuleViolationException("DUPLICATE_PROJECT_PREFIX",
                    "A project with prefix '" + prefix.trim() + "' already exists");
        }
    }
}
