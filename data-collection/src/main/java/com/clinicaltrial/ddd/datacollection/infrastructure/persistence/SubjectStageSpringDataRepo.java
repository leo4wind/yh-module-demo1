package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SubjectStageJpaEntity}.
 */
@Repository
public interface SubjectStageSpringDataRepo extends JpaRepository<SubjectStageJpaEntity, Long> {

    List<SubjectStageJpaEntity> findBySubjectsUserId(Long subjectsUserId);

    Optional<SubjectStageJpaEntity> findBySubjectsUserIdAndStageId(Long subjectsUserId, Long stageId);
}
