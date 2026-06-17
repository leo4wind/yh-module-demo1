package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link CrfAssessmentJpaEntity}.
 */
@Repository
public interface CrfAssessmentSpringDataRepo extends JpaRepository<CrfAssessmentJpaEntity, Long> {

    @Override
    @EntityGraph(attributePaths = "fieldValues")
    Optional<CrfAssessmentJpaEntity> findById(Long id);

    @EntityGraph(attributePaths = "fieldValues")
    List<CrfAssessmentJpaEntity> findBySubjectsStageId(Long subjectsStageId);

    @EntityGraph(attributePaths = "fieldValues")
    List<CrfAssessmentJpaEntity> findBySubjectsUserId(Long subjectsUserId);
}
