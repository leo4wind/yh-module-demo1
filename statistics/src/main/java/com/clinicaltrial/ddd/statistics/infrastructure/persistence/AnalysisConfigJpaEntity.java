package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AlgorithmType;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.ResultStatus;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mirroring the AnalysisConfig domain entity.
 * Maps to the rd_analysis_config table.
 */
@Entity
@Table(name = "rd_analysis_config")
public class AnalysisConfigJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "name", length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "algorithm_type", length = 30)
    private AlgorithmType algorithmType;

    @Column(name = "dependent_variable", length = 100)
    private String dependentVariable;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "rd_analysis_config_independent_vars",
            joinColumns = @JoinColumn(name = "config_id"))
    @Column(name = "variable_name", length = 100)
    private List<String> independentVariables = new ArrayList<>();

    @Column(name = "config_json", length = 2000)
    private String configJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ResultStatus status;

    public AnalysisConfigJpaEntity() {
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

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getDependentVariable() {
        return dependentVariable;
    }

    public void setDependentVariable(String dependentVariable) {
        this.dependentVariable = dependentVariable;
    }

    public List<String> getIndependentVariables() {
        return independentVariables;
    }

    public void setIndependentVariables(List<String> independentVariables) {
        this.independentVariables = independentVariables;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }
}
