package com.clinicaltrial.ddd.dataexport.infrastructure.persistence;

import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFilter.LogicOperator;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFilter.Operator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the ExportFilter domain entity.
 * Maps to the rd_export_filter table.
 */
@Entity
@Table(name = "rd_export_filter")
public class ExportFilterJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "field_code", length = 100)
    private String fieldCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", length = 20)
    private Operator operator;

    @Column(name = "filter_value", length = 500)
    private String filterValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "logic_operator", length = 10)
    private LogicOperator logicOperator;

    public ExportFilterJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(LogicOperator logicOperator) {
        this.logicOperator = logicOperator;
    }
}
