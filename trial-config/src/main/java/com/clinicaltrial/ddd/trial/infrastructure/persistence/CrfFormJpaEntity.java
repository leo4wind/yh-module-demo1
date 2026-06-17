package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.entity.CrfForm} domain entity.
 */
@Entity
@Table(name = "rd_crf_form")
public class CrfFormJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "model_name", nullable = false, length = 200)
    private String modelName;

    @Column(name = "ref_name", length = 200)
    private String refName;

    @Column(name = "rules_name", length = 200)
    private String rulesName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "form_id")
    private List<CrfFieldJpaEntity> fields = new ArrayList<>();

    public CrfFormJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public String getRulesName() {
        return rulesName;
    }

    public void setRulesName(String rulesName) {
        this.rulesName = rulesName;
    }

    public List<CrfFieldJpaEntity> getFields() {
        return fields;
    }

    public void setFields(List<CrfFieldJpaEntity> fields) {
        this.fields = fields;
    }
}
