package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link CrfAssessmentJpaEntity}.
 */
@Repository
public interface CrfAssessmentSpringDataRepo extends JpaRepository<CrfAssessmentJpaEntity, Long> {

    List<CrfAssessmentJpaEntity> findBySubjectsStageId(Long subjectsStageId);

    List<CrfAssessmentJpaEntity> findBySubjectsUserId(Long subjectsUserId);
}
