package com.clinicaltrial.ddd.statistics.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DataImportCompletedEvent — 数据导入完成事件.
 * <p>
 * 当分析项目成功导入数据并完成变量推断时触发。
 * 包含导入的变量数量信息。
 * </p>
 */
public class DataImportCompletedEvent implements DomainEvent {

    private final AnalysisProjectId projectId;
    private final int variableCount;
    private final LocalDateTime occurredOn;

    /**
     * 构造DataImportCompletedEvent.
     *
     * @param projectId     分析项目ID
     * @param variableCount 导入的变量数量
     */
    public DataImportCompletedEvent(AnalysisProjectId projectId, int variableCount) {
        this.projectId = projectId;
        this.variableCount = variableCount;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取分析项目ID.
     *
     * @return AnalysisProjectId
     */
    public AnalysisProjectId getProjectId() {
        return projectId;
    }

    /**
     * 获取导入的变量数量.
     *
     * @return 变量数量
     */
    public int getVariableCount() {
        return variableCount;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "DataImportCompleted: project " + projectId
                + ", variables=" + variableCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataImportCompletedEvent that = (DataImportCompletedEvent) o;
        return variableCount == that.variableCount
                && Objects.equals(projectId, that.projectId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, variableCount, occurredOn);
    }

    @Override
    public String toString() {
        return "DataImportCompletedEvent{"
                + "projectId=" + projectId
                + ", variableCount=" + variableCount
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
