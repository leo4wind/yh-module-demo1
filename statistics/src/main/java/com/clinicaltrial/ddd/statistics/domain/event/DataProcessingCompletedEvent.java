package com.clinicaltrial.ddd.statistics.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DataProcessingCompletedEvent — 数据处理完成事件.
 * <p>
 * 当分析项目的数据预处理步骤全部执行完成时触发。
 * 包含已处理的步骤数量。
 * </p>
 */
public class DataProcessingCompletedEvent implements DomainEvent {

    private final AnalysisProjectId projectId;
    private final int processedStepCount;
    private final LocalDateTime occurredOn;

    /**
     * 构造DataProcessingCompletedEvent.
     *
     * @param projectId         分析项目ID
     * @param processedStepCount 已处理的步骤数量
     */
    public DataProcessingCompletedEvent(AnalysisProjectId projectId, int processedStepCount) {
        this.projectId = projectId;
        this.processedStepCount = processedStepCount;
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
     * 获取已处理的步骤数量.
     *
     * @return 步骤数量
     */
    public int getProcessedStepCount() {
        return processedStepCount;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "DataProcessingCompleted: project " + projectId
                + ", steps=" + processedStepCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataProcessingCompletedEvent that = (DataProcessingCompletedEvent) o;
        return processedStepCount == that.processedStepCount
                && Objects.equals(projectId, that.projectId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, processedStepCount, occurredOn);
    }

    @Override
    public String toString() {
        return "DataProcessingCompletedEvent{"
                + "projectId=" + projectId
                + ", processedStepCount=" + processedStepCount
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
