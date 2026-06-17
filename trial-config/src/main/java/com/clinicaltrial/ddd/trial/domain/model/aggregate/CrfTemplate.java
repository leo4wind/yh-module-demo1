package com.clinicaltrial.ddd.trial.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.AggregateRoot;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.domain.event.CrfTemplateCreatedEvent;
import com.clinicaltrial.ddd.trial.domain.model.entity.CrfForm;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFormId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root for CRF Template (CRF模板).
 * <p>
 * A CRF (Case Report Form) template defines the structure of data collection
 * forms used in a clinical trial. Templates contain one or more {@link CrfForm}
 * entities, each containing {@link com.clinicaltrial.ddd.trial.domain.model.entity.CrfField}
 * entries that capture individual data points.
 * </p>
 *
 * <h3>State Machine</h3>
 * <pre>
 * DRAFT → PUBLISHED
 * </pre>
 *
 * <ul>
 *   <li><strong>DRAFT:</strong> Template is being designed; forms and fields
 *       can be added, modified, or removed.</li>
 *   <li><strong>PUBLISHED:</strong> Template is finalized and available for use
 *       in stage bindings. No further modifications are allowed.</li>
 * </ul>
 *
 * <h3>Copying</h3>
 * Templates can be copied via {@link #copy(String)} to create a new template
 * with the same structure but a different name. This is useful for creating
 * variant forms based on an existing template.
 * </p>
 */
public class CrfTemplate extends AggregateRoot<CrfTemplateId> {

    private CrfTemplateId id;
    private String name;
    private CrfVersionId defaultVersionId;
    private String status;
    private String code;
    private String category;
    private String estimateTime;
    private String notice;
    private String introduce;
    private List<CrfForm> forms;

    /**
     * Default constructor for persistence frameworks.
     */
    protected CrfTemplate() {
        this.forms = new ArrayList<>();
    }

    /**
     * Private constructor used by the factory method.
     */
    private CrfTemplate(CrfTemplateId id, String name, CrfVersionId defaultVersionId,
                         String code, String category, String estimateTime,
                         String notice, String introduce) {
        this.id = id;
        this.name = name;
        this.defaultVersionId = defaultVersionId;
        this.status = "DRAFT";
        this.code = code;
        this.category = category;
        this.estimateTime = estimateTime;
        this.notice = notice;
        this.introduce = introduce;
        this.forms = new ArrayList<>();
    }

    /**
     * Private deep-copy constructor used by {@link #copy(String)}.
     */
    private CrfTemplate(CrfTemplateId newId, String newName, CrfTemplate source) {
        this.id = newId;
        this.name = newName;
        this.defaultVersionId = source.defaultVersionId;
        this.status = "DRAFT";
        this.code = source.code;
        this.category = source.category;
        this.estimateTime = source.estimateTime;
        this.notice = source.notice;
        this.introduce = source.introduce;
        this.forms = new ArrayList<>();
        // Deep copy forms (forms are entities with their own identities;
        // for copy we generate new identities - the caller is responsible
        // for assigning new form/field IDs after the copy)
        if (source.forms != null) {
            for (CrfForm sourceForm : source.forms) {
                CrfFormId newFormId = new CrfFormId(null); // identity to be assigned
                CrfForm copiedForm = CrfForm.create(newFormId,
                        sourceForm.getModelName(),
                        sourceForm.getRefName(),
                        sourceForm.getRulesName());
                // Copy fields from source form (shallow copy of field data)
                sourceForm.getFields().forEach(copiedForm::addField);
                this.forms.add(copiedForm);
            }
        }
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new CrfTemplate aggregate in DRAFT status.
     * <p>
     * Raises a {@link CrfTemplateCreatedEvent} that must be published after persistence.
     * </p>
     *
     * @param id               the unique template identity; must not be null
     * @param name             the template name; must not be blank
     * @param defaultVersionId the default version identity; may be null
     * @param code             the template code; must not be blank
     * @param category         the template category; may be null
     * @param estimateTime     the estimated time to complete the form; may be null
     * @param notice           any notice text for data entry users; may be null
     * @param introduce        an introduction/description of the template; may be null
     * @return a new CrfTemplate instance
     * @throws IllegalArgumentException if id, name, or code is null/blank
     */
    public static CrfTemplate create(CrfTemplateId id, String name,
                                      CrfVersionId defaultVersionId, String code,
                                      String category, String estimateTime,
                                      String notice, String introduce) {
        if (id == null) {
            throw new IllegalArgumentException("CrfTemplate id must not be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("CrfTemplate name must not be blank");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("CrfTemplate code must not be blank");
        }

        CrfTemplate template = new CrfTemplate(id, name.trim(), defaultVersionId,
                code.trim(), category, estimateTime, notice, introduce);

        template.registerEvent(new CrfTemplateCreatedEvent(
                template.id, template.name, LocalDateTime.now()));

        return template;
    }

    // ---------------------------------------------------------------
    // Business methods
    // ---------------------------------------------------------------

    /**
     * Publishes this template, transitioning from DRAFT to PUBLISHED status.
     * <p>
     * Once published, the template's structure (forms and fields) becomes
     * immutable. Published templates can be used for stage bindings.
     * </p>
     *
     * @throws BusinessRuleViolationException if the template is already published
     *                                        or if no forms have been added
     */
    public void publish() {
        if ("PUBLISHED".equals(this.status)) {
            throw new BusinessRuleViolationException("TEMPLATE_ALREADY_PUBLISHED",
                    "CrfTemplate [id=" + this.id + "] is already published");
        }
        if (forms == null || forms.isEmpty()) {
            throw new BusinessRuleViolationException("TEMPLATE_NO_FORMS",
                    "Cannot publish a CrfTemplate with no forms");
        }

        this.status = "PUBLISHED";
    }

    /**
     * Adds a form to this template.
     *
     * @param form the form to add; must not be null
     * @throws BusinessRuleViolationException if the template is already published
     * @throws IllegalArgumentException        if form is null
     */
    public void addForm(CrfForm form) {
        if (form == null) {
            throw new IllegalArgumentException("form must not be null");
        }
        if ("PUBLISHED".equals(this.status)) {
            throw new BusinessRuleViolationException("TEMPLATE_PUBLISHED",
                    "Cannot modify a published CrfTemplate [id=" + this.id + "]");
        }
        this.forms.add(form);
    }

    /**
     * Creates a deep copy of this template with a new name.
     * <p>
     * The copy will be in DRAFT status regardless of the source template's
     * status. Form and field structures are copied, but new identities
     * should be assigned by the caller or repository before persistence.
     * </p>
     *
     * @param newName the name for the new template; must not be blank
     * @return a new CrfTemplate instance with the same structure
     * @throws IllegalArgumentException if newName is null or blank
     */
    public CrfTemplate copy(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New template name must not be blank");
        }

        // New ID will be assigned by the repository on save
        CrfTemplateId newId = new CrfTemplateId(null);
        return new CrfTemplate(newId, newName.trim(), this);
    }

    // ---------------------------------------------------------------
    // Reconstitution factory (persistence only)
    // ---------------------------------------------------------------

    /**
     * Reconstitutes a CrfTemplate aggregate from persistence.
     * <p>
     * Unlike {@link #create(...)}, this factory does not raise domain events.
     * </p>
     *
     * @param id               the template identity
     * @param name             the template name
     * @param defaultVersionId the default version identity
     * @param status           the current status (DRAFT or PUBLISHED)
     * @param code             the template code
     * @param category         the template category
     * @param estimateTime     the estimated completion time
     * @param notice           the notice text
     * @param introduce        the introduction text
     * @param forms            the list of forms
     * @return a fully initialized CrfTemplate
     */
    public static CrfTemplate reconstruct(CrfTemplateId id, String name,
                                            CrfVersionId defaultVersionId,
                                            String status, String code,
                                            String category, String estimateTime,
                                            String notice, String introduce,
                                            List<CrfForm> forms) {
        if (id == null) {
            throw new IllegalArgumentException("CrfTemplate id must not be null for reconstitution");
        }

        CrfTemplate template = new CrfTemplate();
        template.id = id;
        template.name = name;
        template.defaultVersionId = defaultVersionId;
        template.status = status;
        template.code = code;
        template.category = category;
        template.estimateTime = estimateTime;
        template.notice = notice;
        template.introduce = introduce;
        template.forms = forms != null ? new ArrayList<>(forms) : new ArrayList<>();
        return template;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public CrfTemplateId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CrfVersionId getDefaultVersionId() {
        return defaultVersionId;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public String getNotice() {
        return notice;
    }

    public String getIntroduce() {
        return introduce;
    }

    public List<CrfForm> getForms() {
        return forms != null ? Collections.unmodifiableList(forms) : Collections.emptyList();
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(CrfTemplateId id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultVersionId(CrfVersionId defaultVersionId) {
        this.defaultVersionId = defaultVersionId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void setForms(List<CrfForm> forms) {
        this.forms = forms != null ? new ArrayList<>(forms) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "CrfTemplate{id=" + id + ", name='" + name + "', status=" + status + '}';
    }
}
