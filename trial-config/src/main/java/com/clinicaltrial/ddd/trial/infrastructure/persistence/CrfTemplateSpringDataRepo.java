package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link CrfTemplateJpaEntity}.
 */
@Repository
public interface CrfTemplateSpringDataRepo extends JpaRepository<CrfTemplateJpaEntity, Long> {

    List<CrfTemplateJpaEntity> findByCategory(String category);
}
