package com.clinicaltrial.ddd.subject.domain.service;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;

/**
 * Domain service responsible for generating unique, human-readable
 * subject enrollment codes within a project.
 * <p>
 * The generated code follows the format {@code "{projectPrefix}-{seq}"}
 * where the sequence portion is zero-padded to at least 4 digits
 * (e.g. {@code "TRIAL-0001"}).
 * </p>
 * <p>
 * Implementations are expected to be backed by a persistent sequence
 * counter (e.g. a database sequence or Redis atomic increment) to
 * guarantee uniqueness within a project.
 * </p>
 */
public interface SubjectCodeGenerator {

    /**
     * Generates the next subject code for the given project.
     * <p>
     * The project prefix is resolved from the project configuration
     * (typically stored in the TrialConfiguration aggregate in BC1).
     * The sequence number is monotonically incremented and guaranteed
     * to be unique within the project.
     * </p>
     *
     * @param projectId the project for which to generate a code; must not be null
     * @return a new SubjectCode with the next available sequence number
     * @throws IllegalArgumentException if projectId is null
     */
    SubjectCode generateNextCode(ProjectId projectId);
}
