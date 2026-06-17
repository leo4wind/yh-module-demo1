package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * JPA entity mirroring the AnalysisProject aggregate root.
 * Maps to the rd_analysis_project table.
 */
@Entity
@Table(name = "rd_analysis_project")
public class AnalysisProjectJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<VariableDefinitionJpaEntity> variables = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<DataProcessStepJpaEntity> processSteps = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<AnalysisConfigJpaEntity> analysisConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    private List<AnalysisResultJpaEntity> results = new ArrayList<>();

    public AnalysisProjectJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<VariableDefinitionJpaEntity> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableDefinitionJpaEntity> variables) {
        this.variables = variables;
    }

    public List<DataProcessStepJpaEntity> getProcessSteps() {
        return processSteps;
    }

    public void setProcessSteps(List<DataProcessStepJpaEntity> processSteps) {
        this.processSteps = processSteps;
    }

    public List<AnalysisConfigJpaEntity> getAnalysisConfigs() {
        return analysisConfigs;
    }

    public void setAnalysisConfigs(List<AnalysisConfigJpaEntity> analysisConfigs) {
        this.analysisConfigs = analysisConfigs;
    }

    public List<AnalysisResultJpaEntity> getResults() {
        return results;
    }

    public void setResults(List<AnalysisResultJpaEntity> results) {
        this.results = results;
    }
}
