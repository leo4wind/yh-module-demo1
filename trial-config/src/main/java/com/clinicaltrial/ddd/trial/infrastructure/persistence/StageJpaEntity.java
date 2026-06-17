package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.entity.Stage} domain entity.
 */
@Entity
@Table(name = "rd_project_stages")
public class StageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false, length = 20)
    private StageRepeatType repeatType;

    @Column(name = "auto_add")
    private boolean autoAdd;

    @Column(name = "valid")
    private boolean valid;

    @Embedded
    private BaselineIntervalJpa baselineInterval;

    @Embedded
    private WindowPeriodJpa windowPeriod;

    @Column(name = "task_flag", length = 100)
    private String taskFlag;

    public StageJpaEntity() {
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

    public StageRepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(StageRepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public boolean isAutoAdd() {
        return autoAdd;
    }

    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public BaselineIntervalJpa getBaselineInterval() {
        return baselineInterval;
    }

    public void setBaselineInterval(BaselineIntervalJpa baselineInterval) {
        this.baselineInterval = baselineInterval;
    }

    public WindowPeriodJpa getWindowPeriod() {
        return windowPeriod;
    }

    public void setWindowPeriod(WindowPeriodJpa windowPeriod) {
        this.windowPeriod = windowPeriod;
    }

    public String getTaskFlag() {
        return taskFlag;
    }

    public void setTaskFlag(String taskFlag) {
        this.taskFlag = taskFlag;
    }
}
