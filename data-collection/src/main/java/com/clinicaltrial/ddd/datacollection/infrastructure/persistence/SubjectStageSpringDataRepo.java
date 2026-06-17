package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SubjectStageJpaEntity}.
 */
@Repository
public interface SubjectStageSpringDataRepo extends JpaRepository<SubjectStageJpaEntity, Long> {

    @Override
    @EntityGraph(attributePaths = "crfAssessmentRefs")
    Optional<SubjectStageJpaEntity> findById(Long id);

    @EntityGraph(attributePaths = "crfAssessmentRefs")
    List<SubjectStageJpaEntity> findBySubjectsUserId(Long subjectsUserId);

    @EntityGraph(attributePaths = "crfAssessmentRefs")
    Optional<SubjectStageJpaEntity> findBySubjectsUserIdAndStageId(Long subjectsUserId, Long stageId);
}
