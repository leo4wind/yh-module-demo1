package com.clinicaltrial.ddd.dataexport.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for ExportTaskJpaEntity.
 */
@Repository
public interface ExportTaskSpringDataRepo extends JpaRepository<ExportTaskJpaEntity, Long> {

    List<ExportTaskJpaEntity> findByProjectId(Long projectId);

    boolean existsByTaskNameAndProjectId(String taskName, Long projectId);

    boolean existsByTaskNameAndProjectIdAndIdNot(String taskName, Long projectId, Long id);
}
