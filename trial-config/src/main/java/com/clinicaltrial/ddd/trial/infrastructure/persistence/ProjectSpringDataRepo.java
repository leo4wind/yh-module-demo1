package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ProjectJpaEntity}.
 */
@Repository
public interface ProjectSpringDataRepo extends JpaRepository<ProjectJpaEntity, Long> {

    List<ProjectJpaEntity> findByStatus(ProjectStatus status);

    long countByPrefix(String prefix);
}
