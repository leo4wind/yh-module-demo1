package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for AnalysisProjectJpaEntity.
 */
@Repository
public interface AnalysisProjectSpringDataRepo extends JpaRepository<AnalysisProjectJpaEntity, Long> {

    List<AnalysisProjectJpaEntity> findByNameContaining(String name);
}
