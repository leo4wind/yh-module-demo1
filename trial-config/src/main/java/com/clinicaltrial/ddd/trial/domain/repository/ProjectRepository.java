package com.clinicaltrial.ddd.trial.domain.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;

import java.util.List;

/**
 * Repository interface for the {@link Project} aggregate root.
 * <p>
 * Manages persistence of clinical trial projects. The repository follows the
 * DDD pattern where aggregates are loaded and saved as complete consistency
 * boundaries.
 * </p>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@link #findById(ProjectId)} — Returns an aggregate or empty.</li>
 *   <li>{@link #getById(ProjectId)} — Returns an aggregate or throws
 *       {@link AggregateNotFoundException}.</li>
 *   <li>{@link #save(Project)} — Persists a new or updated aggregate.</li>
 *   <li>{@link #findByStatus(com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus)}
 *       — Finds projects by lifecycle status.</li>
 *   <li>{@link #findAll(int, int)} — Paged retrieval of all projects.</li>
 *   <li>{@link #countByPrefix(String)} — Counts projects by prefix code.</li>
 * </ul>
 */
public interface ProjectRepository {

    /**
     * Finds a project by its identity.
     *
     * @param id the project identity; must not be null
     * @return the project, or empty if not found
     */
    java.util.Optional<Project> findById(ProjectId id);

    /**
     * Gets a project by its identity, throwing if not found.
     *
     * @param id the project identity; must not be null
     * @return the project
     * @throws AggregateNotFoundException if the project is not found
     */
    Project getById(ProjectId id);

    /**
     * Persists a project aggregate (insert or update).
     * <p>
     * After successful persistence, the caller should call
     * {@link com.clinicaltrial.ddd.common.infrastructure.EventBus#publishAll(com.clinicaltrial.ddd.common.model.AggregateRoot)}
     * to publish any pending domain events.
     * </p>
     *
     * @param project the project to save; must not be null
     * @return the saved project (with generated ID if new)
     */
    Project save(Project project);

    /**
     * Finds all projects with the specified status.
     *
     * @param status the status to filter by; must not be null
     * @return list of matching projects (empty if none)
     */
    List<Project> findByStatus(com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus status);

    /**
     * Finds all projects with paging support.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return list of projects on the requested page
     */
    List<Project> findAll(int page, int size);

    /**
     * Counts projects with the given prefix code.
     *
     * @param prefix the prefix to count; must not be null
     * @return the number of projects with the given prefix
     */
    long countByPrefix(String prefix);
}
