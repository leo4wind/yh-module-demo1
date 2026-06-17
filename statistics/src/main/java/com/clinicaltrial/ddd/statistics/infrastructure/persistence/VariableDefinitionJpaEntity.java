package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the VariableDefinition domain entity.
 * Maps to the rd_variable_definition table.
 */
@Entity
@Table(name = "rd_variable_definition")
public class VariableDefinitionJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "label", length = 200)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private VariableType type;

    @Column(name = "source_field", length = 100)
    private String sourceField;

    @Column(name = "is_derived")
    private Boolean derived;

    @Column(name = "expression", length = 1000)
    private String expression;

    public VariableDefinitionJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public VariableType getType() {
        return type;
    }

    public void setType(VariableType type) {
        this.type = type;
    }

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public Boolean getDerived() {
        return derived;
    }

    public void setDerived(Boolean derived) {
        this.derived = derived;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
