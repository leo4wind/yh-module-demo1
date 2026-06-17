package com.clinicaltrial.ddd.statistics.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisConfig;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;
import com.clinicaltrial.ddd.statistics.domain.model.entity.DataProcessStep;
import com.clinicaltrial.ddd.statistics.domain.model.entity.VariableDefinition;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisConfigId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisResultId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessStepId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableDefinitionId;
import com.clinicaltrial.ddd.statistics.domain.repository.AnalysisProjectRepository;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.AnalysisConfigJpaEntity;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.AnalysisProjectJpaEntity;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.AnalysisProjectSpringDataRepo;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.AnalysisResultJpaEntity;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.DataProcessStepJpaEntity;
import com.clinicaltrial.ddd.statistics.infrastructure.persistence.VariableDefinitionJpaEntity;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for AnalysisProject aggregate.
 * Maps between domain AnalysisProject aggregate and JPA entities.
 */
@Repository
@Transactional
public class AnalysisProjectRepositoryImpl implements AnalysisProjectRepository {

    private final AnalysisProjectSpringDataRepo springDataRepo;

    public AnalysisProjectRepositoryImpl(AnalysisProjectSpringDataRepo springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnalysisProject> findById(AnalysisProjectId id) {
        return springDataRepo.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public AnalysisProject save(AnalysisProject project) {
        AnalysisProjectJpaEntity entity = toJpa(project);
        AnalysisProjectJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(AnalysisProjectId id) {
        springDataRepo.deleteById(id.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalysisProject> findAll() {
        return springDataRepo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalysisProject> findByNameContaining(String name) {
        return springDataRepo.findByNameContaining(name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ========== Domain -> JPA mapping ==========

    private AnalysisProjectJpaEntity toJpa(AnalysisProject domain) {
        AnalysisProjectJpaEntity entity = new AnalysisProjectJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());

        if (domain.getVariables() != null) {
            entity.setVariables(domain.getVariables().stream()
                    .map(v -> toJpaVariable(v, entity))
                    .collect(Collectors.toList()));
        }

        if (domain.getProcessSteps() != null) {
            entity.setProcessSteps(domain.getProcessSteps().stream()
                    .map(s -> toJpaProcessStep(s, entity))
                    .collect(Collectors.toList()));
        }

        if (domain.getAnalysisConfigs() != null) {
            entity.setAnalysisConfigs(domain.getAnalysisConfigs().stream()
                    .map(c -> toJpaAnalysisConfig(c, entity))
                    .collect(Collectors.toList()));
        }

        if (domain.getResults() != null) {
            entity.setResults(domain.getResults().stream()
                    .map(r -> toJpaAnalysisResult(r, entity))
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    private VariableDefinitionJpaEntity toJpaVariable(VariableDefinition domain,
                                                       AnalysisProjectJpaEntity parent) {
        VariableDefinitionJpaEntity entity = new VariableDefinitionJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setProjectId(parent.getId());
        entity.setName(domain.getName());
        entity.setLabel(domain.getLabel());
        entity.setType(domain.getVariableType());
        entity.setSourceField(domain.getSourceField());
        entity.setDerived(domain.isDerived());
        entity.setExpression(domain.getExpression());
        return entity;
    }

    private DataProcessStepJpaEntity toJpaProcessStep(DataProcessStep domain,
                                                       AnalysisProjectJpaEntity parent) {
        DataProcessStepJpaEntity entity = new DataProcessStepJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setProjectId(parent.getId());
        entity.setProcessType(domain.getProcessType());
        entity.setConfigJson(domain.getConfigJson());
        entity.setSortOrder(domain.getSortOrder());
        return entity;
    }

    private AnalysisConfigJpaEntity toJpaAnalysisConfig(AnalysisConfig domain,
                                                         AnalysisProjectJpaEntity parent) {
        AnalysisConfigJpaEntity entity = new AnalysisConfigJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setProjectId(parent.getId());
        entity.setName(domain.getName());
        entity.setAlgorithmType(domain.getAlgorithmType());
        entity.setDependentVariable(domain.getDependentVariable());
        entity.setIndependentVariables(domain.getIndependentVariables() != null
                ? new java.util.ArrayList<>(domain.getIndependentVariables())
                : new java.util.ArrayList<String>());
        entity.setConfigJson(domain.getConfigJson());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    private AnalysisResultJpaEntity toJpaAnalysisResult(AnalysisResult domain,
                                                         AnalysisProjectJpaEntity parent) {
        AnalysisResultJpaEntity entity = new AnalysisResultJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setProjectId(parent.getId());
        entity.setName(domain.getName());
        entity.setMethod(domain.getMethod());
        entity.setData(domain.getData());
        entity.setResultSummary(domain.getResultSummary());
        entity.setParams(domain.getParams());
        entity.setIsFavorite(domain.isFavorite());
        entity.setCreateTime(domain.getCreateTime());
        return entity;
    }

    // ========== JPA -> Domain mapping ==========

    private AnalysisProject toDomain(AnalysisProjectJpaEntity entity) {
        List<VariableDefinition> variables = entity.getVariables() != null
                ? entity.getVariables().stream().map(this::toDomainVariable)
                    .collect(Collectors.toList())
                : Collections.<VariableDefinition>emptyList();

        List<DataProcessStep> processSteps = entity.getProcessSteps() != null
                ? entity.getProcessSteps().stream().map(this::toDomainProcessStep)
                    .collect(Collectors.toList())
                : Collections.<DataProcessStep>emptyList();

        List<AnalysisConfig> analysisConfigs = entity.getAnalysisConfigs() != null
                ? entity.getAnalysisConfigs().stream().map(this::toDomainAnalysisConfig)
                    .collect(Collectors.toList())
                : Collections.<AnalysisConfig>emptyList();

        List<AnalysisResult> results = entity.getResults() != null
                ? entity.getResults().stream().map(this::toDomainAnalysisResult)
                    .collect(Collectors.toList())
                : Collections.<AnalysisResult>emptyList();

        return AnalysisProject.reconstruct(
                new AnalysisProjectId(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                variables,
                processSteps,
                analysisConfigs,
                results
        );
    }

    private VariableDefinition toDomainVariable(VariableDefinitionJpaEntity entity) {
        VariableDefinition vd;
        if (Boolean.TRUE.equals(entity.getDerived())) {
            vd = VariableDefinition.createDerived(
                    new VariableDefinitionId(entity.getId()),
                    entity.getName(),
                    entity.getLabel(),
                    entity.getType(),
                    entity.getExpression()
            );
        } else {
            vd = VariableDefinition.create(
                    new VariableDefinitionId(entity.getId()),
                    entity.getName(),
                    entity.getLabel(),
                    entity.getType(),
                    entity.getSourceField()
            );
        }
        return vd;
    }

    private DataProcessStep toDomainProcessStep(DataProcessStepJpaEntity entity) {
        return DataProcessStep.create(
                new DataProcessStepId(entity.getId()),
                entity.getProcessType(),
                entity.getConfigJson(),
                entity.getSortOrder()
        );
    }

    private AnalysisConfig toDomainAnalysisConfig(AnalysisConfigJpaEntity entity) {
        return AnalysisConfig.create(
                new AnalysisConfigId(entity.getId()),
                entity.getName(),
                entity.getAlgorithmType(),
                entity.getDependentVariable(),
                entity.getIndependentVariables(),
                entity.getConfigJson()
        );
    }

    private AnalysisResult toDomainAnalysisResult(AnalysisResultJpaEntity entity) {
        AnalysisResult result = AnalysisResult.create(
                new AnalysisResultId(entity.getId()),
                entity.getName(),
                entity.getMethod(),
                entity.getData(),
                entity.getResultSummary(),
                entity.getParams()
        );
        if (entity.getIsFavorite() != null && entity.getIsFavorite()) {
            result.markAsFavorite();
        }
        return result;
    }
}
