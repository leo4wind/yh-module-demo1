package com.clinicaltrial.ddd.subject.infrastructure.persistence;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link SubjectJpaEntity}.
 */
@Repository
public interface SubjectSpringDataRepo extends JpaRepository<SubjectJpaEntity, Long> {

    List<SubjectJpaEntity> findByProjectId(Long projectId);

    List<SubjectJpaEntity> findByStatus(SubjectStatus status);

    long countByProjectId(Long projectId);
}
