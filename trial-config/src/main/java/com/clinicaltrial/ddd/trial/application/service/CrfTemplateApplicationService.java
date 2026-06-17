package com.clinicaltrial.ddd.trial.application.service;

import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.application.command.CreateCrfTemplateCommand;
import com.clinicaltrial.ddd.trial.application.command.UpdateCrfTemplateCommand;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.CrfTemplate;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfField;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfFieldOption;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfForm;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldOptionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFormId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType;
import com.clinicaltrial.ddd.trial.domain.repository.CrfTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Application service for CrfTemplate aggregate use cases.
 * <p>
 * Orchestrates the creation, publishing, and copying of CRF templates.
 * This service follows the DDD application service pattern: it receives
 * commands, delegates to domain objects for business logic, persists via
 * the repository, and publishes domain events through the event bus.
 * </p>
 *
 * <h3>Use Cases</h3>
 * <ul>
 *   <li>{@link #createTemplate(CreateCrfTemplateCommand)} — Create a new template</li>
 *   <li>{@link #publishTemplate(CrfTemplateId)} — Publish a DRAFT template</li>
 *   <li>{@link #copyTemplate(CrfTemplateId, String)} — Deep-copy a template</li>
 * </ul>
 */
@Service
public class CrfTemplateApplicationService {

    private final CrfTemplateRepository crfTemplateRepository;
    private final EventBus eventBus;

    /**
     * Constructs the application service with its required dependencies.
     *
     * @param crfTemplateRepository the repository for CrfTemplate aggregates
     * @param eventBus              the infrastructure event bus
     */
    public CrfTemplateApplicationService(CrfTemplateRepository crfTemplateRepository,
                                          EventBus eventBus) {
        this.crfTemplateRepository = crfTemplateRepository;
        this.eventBus = eventBus;
    }

    /**
     * Creates a new CRF template in DRAFT status.
     * <p>
     * Creates the CrfTemplate aggregate, persists it, and publishes the
     * {@link com.clinicaltrial.ddd.trial.domain.event.CrfTemplateCreatedEvent}.
     * </p>
     *
     * @param command the create-template command
     * @return the created template
     * @throws IllegalArgumentException         if command is null
     * @throws BusinessRuleViolationException    if business rules are violated
     */
    @Transactional
    public CrfTemplate createTemplate(CreateCrfTemplateCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        CrfTemplate template = CrfTemplate.create(
                new CrfTemplateId(generateNumericId()),
                command.getName(),
                new CrfVersionId(1L),
                command.getCode(),
                command.getCategory(),
                command.getEstimateTime(),
                command.getNotice(),
                command.getIntroduce());

        // Persist
        CrfTemplate savedTemplate = crfTemplateRepository.save(template);

        // Publish events
        eventBus.publishAll(savedTemplate);

        return savedTemplate;
    }

    /**
     * Replaces the editable structure of a DRAFT template.
     */
    @Transactional
    public CrfTemplate updateTemplate(CrfTemplateId templateId, UpdateCrfTemplateCommand command) {
        if (templateId == null) {
            throw new IllegalArgumentException("templateId must not be null");
        }
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        CrfTemplate existing = crfTemplateRepository.getById(templateId);
        if ("PUBLISHED".equals(existing.getStatus())) {
            throw new BusinessRuleViolationException("TEMPLATE_PUBLISHED",
                    "Published CRF template cannot be edited; copy it as a draft first");
        }

        CrfTemplate replacement = CrfTemplate.reconstruct(
                existing.getId(),
                command.getName(),
                existing.getDefaultVersionId() != null ? existing.getDefaultVersionId() : new CrfVersionId(1L),
                existing.getStatus(),
                command.getCode(),
                command.getCategory(),
                command.getEstimateTime(),
                command.getNotice(),
                command.getIntroduce(),
                toForms(command.getForms()));

        return crfTemplateRepository.save(replacement);
    }

    /**
     * Publishes a CRF template, transitioning it from DRAFT to PUBLISHED status.
     * <p>
     * Loads the template, calls the domain {@link CrfTemplate#publish()} method,
     * persists the change.
     * </p>
     *
     * @param templateId the identity of the template to publish
     * @return the published template
     * @throws BusinessRuleViolationException if the template cannot be published
     */
    @Transactional
    public CrfTemplate publishTemplate(CrfTemplateId templateId) {
        if (templateId == null) {
            throw new IllegalArgumentException("templateId must not be null");
        }

        CrfTemplate template = crfTemplateRepository.getById(templateId);
        validatePublishable(template);
        template.publish();

        CrfTemplate savedTemplate = crfTemplateRepository.save(template);
        eventBus.publishAll(savedTemplate);

        return savedTemplate;
    }

    /**
     * Creates a deep copy of a CRF template with a new name.
     * <p>
     * Loads the source template, delegates to {@link CrfTemplate#copy(String)},
     * persists the new template, and publishes events from the new template.
     * </p>
     *
     * @param templateId the identity of the template to copy
     * @param newName    the name for the new template
     * @return the newly created copy
     * @throws BusinessRuleViolationException if the template cannot be copied
     */
    @Transactional
    public CrfTemplate copyTemplate(CrfTemplateId templateId, String newName) {
        if (templateId == null) {
            throw new IllegalArgumentException("templateId must not be null");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("newName must not be blank");
        }

        CrfTemplate source = crfTemplateRepository.getById(templateId);
        CrfTemplate copiedTemplate = CrfTemplate.reconstruct(
                new CrfTemplateId(generateNumericId()),
                newName.trim(),
                new CrfVersionId(1L),
                "DRAFT",
                source.getCode() + "_COPY_" + System.currentTimeMillis(),
                source.getCategory(),
                source.getEstimateTime(),
                source.getNotice(),
                source.getIntroduce(),
                cloneForms(source.getForms()));

        // Persist the new template
        CrfTemplate savedTemplate = crfTemplateRepository.save(copiedTemplate);

        // Publish events from the new template (includes CrfTemplateCreatedEvent)
        eventBus.publishAll(savedTemplate);

        return savedTemplate;
    }

    private List<CrfForm> toForms(List<UpdateCrfTemplateCommand.FormCommand> commands) {
        List<CrfForm> forms = new ArrayList<>();
        if (commands == null) {
            return forms;
        }
        for (UpdateCrfTemplateCommand.FormCommand command : commands) {
            CrfForm form = CrfForm.create(
                    new CrfFormId(generateNumericId()),
                    command.getModelName(),
                    command.getRefName(),
                    command.getRulesName());
            for (CrfField field : toFields(command.getFields())) {
                form.addField(field);
            }
            forms.add(form);
        }
        return forms;
    }

    private List<CrfField> toFields(List<UpdateCrfTemplateCommand.FieldCommand> commands) {
        List<CrfField> fields = new ArrayList<>();
        if (commands == null) {
            return fields;
        }
        for (UpdateCrfTemplateCommand.FieldCommand command : commands) {
            FieldType fieldType = command.getFieldType();
            CrfField field = CrfField.create(
                    new CrfFieldId(generateNumericId()),
                    command.getFieldCode(),
                    command.getFieldLabel(),
                    fieldType,
                    command.getDefaultValue(),
                    command.getDataUnit(),
                    command.isRequired(),
                    command.isHidden(),
                    command.getSortOrder());
            if (supportsOptions(fieldType)) {
                for (CrfFieldOption option : toOptions(command.getOptions())) {
                    field.addOption(option);
                }
            }
            fields.add(field);
        }
        return fields;
    }

    private List<CrfFieldOption> toOptions(List<UpdateCrfTemplateCommand.OptionCommand> commands) {
        List<CrfFieldOption> options = new ArrayList<>();
        if (commands == null) {
            return options;
        }
        for (UpdateCrfTemplateCommand.OptionCommand command : commands) {
            options.add(CrfFieldOption.create(
                    new CrfFieldOptionId(generateNumericId()),
                    command.getOptionLabel(),
                    command.getOptionValue(),
                    command.getSortOrder(),
                    command.getScore()));
        }
        return options;
    }

    private List<CrfForm> cloneForms(List<CrfForm> sourceForms) {
        List<CrfForm> forms = new ArrayList<>();
        for (CrfForm sourceForm : sourceForms) {
            CrfForm form = CrfForm.create(
                    new CrfFormId(generateNumericId()),
                    sourceForm.getModelName(),
                    sourceForm.getRefName(),
                    sourceForm.getRulesName());
            for (CrfField sourceField : sourceForm.getFields()) {
                CrfField field = CrfField.create(
                        new CrfFieldId(generateNumericId()),
                        sourceField.getFieldCode(),
                        sourceField.getFieldLabel(),
                        sourceField.getFieldType(),
                        sourceField.getDefaultValue(),
                        sourceField.getDataUnit(),
                        sourceField.isRequired(),
                        sourceField.isHidden(),
                        sourceField.getSortOrder());
                for (CrfFieldOption sourceOption : sourceField.getOptions()) {
                    field.addOption(CrfFieldOption.create(
                            new CrfFieldOptionId(generateNumericId()),
                            sourceOption.getOptionLabel(),
                            sourceOption.getOptionValue(),
                            sourceOption.getSortOrder(),
                            sourceOption.getScore()));
                }
                form.addField(field);
            }
            forms.add(form);
        }
        return forms;
    }

    private void validatePublishable(CrfTemplate template) {
        if (template.getForms().isEmpty()) {
            throw new BusinessRuleViolationException("TEMPLATE_NO_FORMS",
                    "Cannot publish a CRF template with no forms");
        }
        boolean hasField = template.getForms().stream()
                .anyMatch(form -> !form.getFields().isEmpty());
        if (!hasField) {
            throw new BusinessRuleViolationException("TEMPLATE_NO_FIELDS",
                    "Cannot publish a CRF template with no fields");
        }
    }

    private boolean supportsOptions(FieldType fieldType) {
        return fieldType == FieldType.SELECT
                || fieldType == FieldType.RADIO
                || fieldType == FieldType.CHECKBOX;
    }

    private Long generateNumericId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
