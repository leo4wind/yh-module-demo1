package com.clinicaltrial.ddd.trial.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.CrfTemplate;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfField;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfFieldOption;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfForm;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldOptionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFormId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.repository.CrfTemplateRepository;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfFieldJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfFieldOptionJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfFormJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfTemplateJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.CrfTemplateSpringDataRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for {@link CrfTemplate} aggregate.
 * Maps between domain CrfTemplate aggregate and JPA entities.
 */
@Repository
@Transactional
public class CrfTemplateRepositoryImpl implements CrfTemplateRepository {

    private final CrfTemplateSpringDataRepo springDataRepo;

    public CrfTemplateRepositoryImpl(CrfTemplateSpringDataRepo springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CrfTemplate> findById(CrfTemplateId id) {
        return springDataRepo.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public CrfTemplate getById(CrfTemplateId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("CrfTemplate", id.getValue()));
    }

    @Override
    @Transactional
    public CrfTemplate save(CrfTemplate template) {
        CrfTemplateJpaEntity entity = toJpa(template);
        CrfTemplateJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrfTemplate> findAll(int page, int size) {
        return springDataRepo.findAll(PageRequest.of(page, size))
                .getContent().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrfTemplate> findByCategory(String category) {
        return springDataRepo.findByCategory(category).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ========== Domain -> JPA mapping ==========

    private CrfTemplateJpaEntity toJpa(CrfTemplate domain) {
        CrfTemplateJpaEntity entity = new CrfTemplateJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setName(domain.getName());
        entity.setDefaultVersionId(domain.getDefaultVersionId() != null ? domain.getDefaultVersionId().getValue() : null);
        entity.setStatus(domain.getStatus());
        entity.setCode(domain.getCode());
        entity.setCategory(domain.getCategory());
        entity.setEstimateTime(domain.getEstimateTime());
        entity.setNotice(domain.getNotice());
        entity.setIntroduce(domain.getIntroduce());
        entity.setForms(toJpaForms(domain.getForms(), entity.getId()));
        return entity;
    }

    private List<CrfFormJpaEntity> toJpaForms(List<CrfForm> forms, Long templateId) {
        if (forms == null) return Collections.emptyList();
        return forms.stream()
                .map(f -> toJpaForm(f, templateId))
                .collect(Collectors.toList());
    }

    private CrfFormJpaEntity toJpaForm(CrfForm form, Long templateId) {
        CrfFormJpaEntity entity = new CrfFormJpaEntity();
        entity.setId(form.getId() != null ? form.getId().getValue() : null);
        entity.setTemplateId(templateId);
        entity.setModelName(form.getModelName());
        entity.setRefName(form.getRefName());
        entity.setRulesName(form.getRulesName());
        entity.setFields(toJpaFields(form.getFields()));
        return entity;
    }

    private List<CrfFieldJpaEntity> toJpaFields(List<CrfField> fields) {
        if (fields == null) return Collections.emptyList();
        return fields.stream()
                .map(this::toJpaField)
                .collect(Collectors.toList());
    }

    private CrfFieldJpaEntity toJpaField(CrfField field) {
        CrfFieldJpaEntity entity = new CrfFieldJpaEntity();
        entity.setId(field.getId() != null ? field.getId().getValue() : null);
        entity.setFieldCode(field.getFieldCode());
        entity.setFieldLabel(field.getFieldLabel());
        entity.setFieldType(field.getFieldType());
        entity.setDefaultValue(field.getDefaultValue());
        entity.setDataUnit(field.getDataUnit());
        entity.setRequired(field.isRequired());
        entity.setHidden(field.isHidden());
        entity.setSortOrder(field.getSortOrder());
        entity.setValidationRules(null); // validationRules Map is not stored as JPA column; handled separately if needed
        entity.setOptions(toJpaOptions(field.getOptions()));
        return entity;
    }

    private List<CrfFieldOptionJpaEntity> toJpaOptions(List<CrfFieldOption> options) {
        if (options == null) return Collections.emptyList();
        return options.stream()
                .map(this::toJpaOption)
                .collect(Collectors.toList());
    }

    private CrfFieldOptionJpaEntity toJpaOption(CrfFieldOption option) {
        CrfFieldOptionJpaEntity entity = new CrfFieldOptionJpaEntity();
        entity.setId(option.getId() != null ? option.getId().getValue() : null);
        entity.setOptionLabel(option.getOptionLabel());
        entity.setOptionValue(option.getOptionValue());
        entity.setSortOrder(option.getSortOrder());
        entity.setScore(option.getScore());
        return entity;
    }

    // ========== JPA -> Domain mapping ==========

    private CrfTemplate toDomain(CrfTemplateJpaEntity entity) {
        return CrfTemplate.reconstruct(
                new CrfTemplateId(entity.getId()),
                entity.getName(),
                entity.getDefaultVersionId() != null ? new CrfVersionId(entity.getDefaultVersionId()) : null,
                entity.getStatus(),
                entity.getCode(),
                entity.getCategory(),
                entity.getEstimateTime(),
                entity.getNotice(),
                entity.getIntroduce(),
                toDomainForms(entity.getForms())
        );
    }

    private List<CrfForm> toDomainForms(List<CrfFormJpaEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(this::toDomainForm)
                .collect(Collectors.toList());
    }

    private CrfForm toDomainForm(CrfFormJpaEntity entity) {
        CrfForm form = CrfForm.create(
                new CrfFormId(entity.getId()),
                entity.getModelName(),
                entity.getRefName(),
                entity.getRulesName()
        );
        List<CrfField> fields = toDomainFields(entity.getFields());
        for (CrfField field : fields) {
            form.addField(field);
        }
        return form;
    }

    private List<CrfField> toDomainFields(List<CrfFieldJpaEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(this::toDomainField)
                .collect(Collectors.toList());
    }

    private CrfField toDomainField(CrfFieldJpaEntity entity) {
        CrfField field = CrfField.create(
                new CrfFieldId(entity.getId()),
                entity.getFieldCode(),
                entity.getFieldLabel(),
                entity.getFieldType(),
                entity.getDefaultValue(),
                entity.getDataUnit(),
                entity.isRequired(),
                entity.isHidden(),
                entity.getSortOrder()
        );
        List<CrfFieldOption> options = toDomainOptions(entity.getOptions());
        for (CrfFieldOption option : options) {
            field.addOption(option);
        }
        return field;
    }

    private List<CrfFieldOption> toDomainOptions(List<CrfFieldOptionJpaEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(this::toDomainOption)
                .collect(Collectors.toList());
    }

    private CrfFieldOption toDomainOption(CrfFieldOptionJpaEntity entity) {
        return CrfFieldOption.create(
                new CrfFieldOptionId(entity.getId()),
                entity.getOptionLabel(),
                entity.getOptionValue(),
                entity.getSortOrder(),
                entity.getScore()
        );
    }
}
