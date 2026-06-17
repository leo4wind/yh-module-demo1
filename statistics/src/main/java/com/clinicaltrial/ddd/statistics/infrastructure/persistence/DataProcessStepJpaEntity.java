package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the DataProcessStep domain entity.
 * Maps to the rd_data_process_step table.
 */
@Entity
@Table(name = "rd_data_process_step")
public class DataProcessStepJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_type", length = 30)
    private DataProcessType processType;

    @Column(name = "config_json", length = 2000)
    private String configJson;

    @Column(name = "sort_order")
    private Integer sortOrder;

    public DataProcessStepJpaEntity() {
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

    public DataProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(DataProcessType processType) {
        this.processType = processType;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
