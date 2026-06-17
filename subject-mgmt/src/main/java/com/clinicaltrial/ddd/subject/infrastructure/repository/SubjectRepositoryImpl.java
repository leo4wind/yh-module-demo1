package com.clinicaltrial.ddd.subject.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectFallOffReason;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;
import com.clinicaltrial.ddd.subject.domain.repository.SubjectRepository;
import com.clinicaltrial.ddd.subject.infrastructure.persistence.ScreeningInfoJpa;
import com.clinicaltrial.ddd.subject.infrastructure.persistence.SubjectFallOffReasonJpa;
import com.clinicaltrial.ddd.subject.infrastructure.persistence.SubjectJpaEntity;
import com.clinicaltrial.ddd.subject.infrastructure.persistence.SubjectSpringDataRepo;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for {@link Subject} aggregate.
 * Maps between domain Subject aggregate and JPA entities.
 */
@Repository
@Transactional
public class SubjectRepositoryImpl implements SubjectRepository {

    private final SubjectSpringDataRepo springDataRepo;

    public SubjectRepositoryImpl(SubjectSpringDataRepo springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subject> findById(SubjectId subjectId) {
        return springDataRepo.findById(subjectId.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Subject getById(SubjectId subjectId) {
        return findById(subjectId)
                .orElseThrow(() -> new AggregateNotFoundException("Subject", subjectId.getValue()));
    }

    @Override
    @Transactional
    public Subject save(Subject subject) {
        SubjectJpaEntity entity = toJpa(subject);
        SubjectJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByProjectId(ProjectId projectId) {
        return springDataRepo.findByProjectId(projectId.getValue()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByStatus(SubjectStatus status) {
        return springDataRepo.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByProjectId(ProjectId projectId) {
        return springDataRepo.countByProjectId(projectId.getValue());
    }

    // ========== Domain -> JPA mapping ==========

    private SubjectJpaEntity toJpa(Subject domain) {
        SubjectJpaEntity entity = new SubjectJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setProjectId(domain.getProjectId() != null ? domain.getProjectId().getValue() : null);
        entity.setCode(domain.getCode() != null ? domain.getCode().getFullCode() : null);
        entity.setStatus(domain.getStatus());
        entity.setUserId(domain.getUserId());
        entity.setSiteId(domain.getSiteId());
        entity.setBlh(domain.getBlh());
        entity.setSyxh(domain.getSyxh());
        entity.setName(domain.getName());
        entity.setGender(domain.getGender());
        entity.setAge(domain.getAge());
        entity.setGroupSubsetIds(domain.getGroupSubsetIds() != null
                ? new java.util.ArrayList<>(domain.getGroupSubsetIds())
                : new java.util.ArrayList<String>());
        entity.setScreeningInfo(toJpaScreeningInfo(domain.getScreeningInfo()));
        entity.setFallOffReason(toJpaFallOffReason(domain.getFallOffReason()));
        entity.setRemarks(domain.getRemarks());
        entity.setTrackDownId(domain.getTrackDownId());
        entity.setSupervisorId(domain.getSupervisorId());
        return entity;
    }

    private ScreeningInfoJpa toJpaScreeningInfo(ScreeningInfo info) {
        if (info == null) return null;
        return new ScreeningInfoJpa(
                info.getScreeningDate(),
                info.getScreeningResult() != null ? info.getScreeningResult().name() : null,
                info.getRemarks()
        );
    }

    private SubjectFallOffReasonJpa toJpaFallOffReason(SubjectFallOffReason reason) {
        if (reason == null) return null;
        return new SubjectFallOffReasonJpa(
                reason.getReasonCode(),
                reason.getReasonDescription(),
                reason.getFallOffDate()
        );
    }

    // ========== JPA -> Domain mapping ==========

    private Subject toDomain(SubjectJpaEntity entity) {
        SubjectCode code = null;
        if (entity.getCode() != null) {
            // Parse "PREFIX-0001" format back to SubjectCode
            int dashIndex = entity.getCode().lastIndexOf('-');
            if (dashIndex > 0 && dashIndex + 1 < entity.getCode().length()) {
                String seqPart = entity.getCode().substring(dashIndex + 1);
                try {
                    int seq = Integer.parseInt(seqPart);
                    String prefix = entity.getCode().substring(0, dashIndex);
                    code = new SubjectCode(prefix, seq);
                } catch (NumberFormatException e) {
                    // If the suffix after '-' is not a valid integer, leave code null
                    code = null;
                }
            }
        }

        return Subject.reconstruct(
                new SubjectId(entity.getId()),
                new ProjectId(entity.getProjectId()),
                code,
                entity.getStatus(),
                entity.getUserId(),
                entity.getSiteId(),
                entity.getBlh(),
                entity.getSyxh(),
                entity.getName(),
                entity.getGender(),
                entity.getAge(),
                entity.getGroupSubsetIds() != null
                        ? new java.util.ArrayList<>(entity.getGroupSubsetIds())
                        : new java.util.ArrayList<String>(),
                toDomainScreeningInfo(entity.getScreeningInfo()),
                toDomainFallOffReason(entity.getFallOffReason()),
                entity.getRemarks(),
                entity.getTrackDownId(),
                entity.getSupervisorId()
        );
    }

    private ScreeningInfo toDomainScreeningInfo(ScreeningInfoJpa jpa) {
        if (jpa == null) return null;
        ScreeningInfo.ScreeningResult result = null;
        if (jpa.getScreeningResult() != null) {
            result = ScreeningInfo.ScreeningResult.valueOf(jpa.getScreeningResult());
        }
        return new ScreeningInfo(jpa.getScreeningDate(), result, jpa.getRemarks());
    }

    private SubjectFallOffReason toDomainFallOffReason(SubjectFallOffReasonJpa jpa) {
        if (jpa == null) return null;
        return new SubjectFallOffReason(
                jpa.getReasonCode(),
                jpa.getReasonDescription(),
                jpa.getFallOffDate()
        );
    }
}
