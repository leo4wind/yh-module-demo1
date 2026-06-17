package com.clinicaltrial.ddd.subject.infrastructure.persistence;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SubjectJpaEntity}.
 */
@Repository
public interface SubjectSpringDataRepo extends JpaRepository<SubjectJpaEntity, Long> {

    @Override
    @EntityGraph(attributePaths = "groupSubsetIds")
    Optional<SubjectJpaEntity> findById(Long id);

    @EntityGraph(attributePaths = "groupSubsetIds")
    List<SubjectJpaEntity> findByProjectId(Long projectId);

    @EntityGraph(attributePaths = "groupSubsetIds")
    List<SubjectJpaEntity> findByStatus(SubjectStatus status);

    long countByProjectId(Long projectId);
}
