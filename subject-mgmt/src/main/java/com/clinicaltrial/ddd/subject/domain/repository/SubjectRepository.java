package com.clinicaltrial.ddd.subject.domain.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the Subject aggregate.
 * <p>
 * Follows the DDD repository pattern: provides collection-like access to
 * Subject aggregates. Implementations are responsible for persistence
 * (typically via JPA/MyBatis) and for publishing domain events after
 * successful save operations.
 * </p>
 * <p>
 * All methods throw {@link AggregateNotFoundException} when an expected
 * aggregate cannot be found.
 * </p>
 */
public interface SubjectRepository {

    /**
     * Finds a subject by its unique identity.
     *
     * @param subjectId the subject identity; must not be null
     * @return an Optional containing the subject, or empty if not found
     * @throws IllegalArgumentException if subjectId is null
     */
    Optional<Subject> findById(SubjectId subjectId);

    /**
     * Finds a subject by its unique identity, throwing if not found.
     *
     * @param subjectId the subject identity; must not be null
     * @return the subject
     * @throws AggregateNotFoundException if no subject with the given id exists
     * @throws IllegalArgumentException   if subjectId is null
     */
    default Subject getById(SubjectId subjectId) {
        return findById(subjectId)
                .orElseThrow(() -> new AggregateNotFoundException("Subject", subjectId.getValue()));
    }

    /**
     * Saves (creates or updates) a subject aggregate.
     * <p>
     * After successful persistence, the implementation should call
     * {@link Subject#pullDomainEvents()} to retrieve and publish any
     * pending domain events via the EventBus.
     * </p>
     *
     * @param subject the subject aggregate to save; must not be null
     * @return the saved subject (potentially with generated identity)
     * @throws IllegalArgumentException if subject is null
     */
    Subject save(Subject subject);

    /**
     * Finds all subjects belonging to a given project.
     *
     * @param projectId the project identity; must not be null
     * @return a list of subjects for the project (never null; empty if none)
     * @throws IllegalArgumentException if projectId is null
     */
    List<Subject> findByProjectId(ProjectId projectId);

    /**
     * Finds all subjects with the given lifecycle status.
     *
     * @param status the status to filter by; may be null to return subjects
     *               with no status assigned yet
     * @return a list of matching subjects (never null; empty if none)
     */
    List<Subject> findByStatus(SubjectStatus status);

    /**
     * Returns the count of subjects currently enrolled in the given project.
     *
     * @param projectId the project identity; must not be null
     * @return the number of subjects enrolled in the project
     * @throws IllegalArgumentException if projectId is null
     */
    long countByProjectId(ProjectId projectId);
}
