package com.clinicaltrial.ddd.query.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for QueryJpaEntity.
 */
@Repository
public interface QuerySpringDataRepo extends JpaRepository<QueryJpaEntity, Long> {

    List<QueryJpaEntity> findByAssessmentId(Long assessmentId);

    int countByAssessmentIdAndStatus(Long assessmentId,
                                     com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus status);
}
