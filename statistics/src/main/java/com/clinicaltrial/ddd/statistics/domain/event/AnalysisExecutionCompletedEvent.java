package com.clinicaltrial.ddd.statistics.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisConfigId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisResultId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AnalysisExecutionCompletedEvent — 分析执行完成事件.
 * <p>
 * 当一次统计分析执行完成并生成结果时触发。
 * 携带分析配置ID和生成的结果ID。
 * </p>
 */
public class AnalysisExecutionCompletedEvent implements DomainEvent {

    private final AnalysisProjectId projectId;
    private final AnalysisConfigId configId;
    private final AnalysisResultId resultId;
    private final LocalDateTime occurredOn;

    /**
     * 构造AnalysisExecutionCompletedEvent.
     *
     * @param projectId 分析项目ID
     * @param configId  分析配置ID
     * @param resultId  生成的分析结果ID
     */
    public AnalysisExecutionCompletedEvent(AnalysisProjectId projectId,
                                            AnalysisConfigId configId,
                                            AnalysisResultId resultId) {
        this.projectId = projectId;
        this.configId = configId;
        this.resultId = resultId;
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
     * 获取分析配置ID.
     *
     * @return AnalysisConfigId
     */
    public AnalysisConfigId getConfigId() {
        return configId;
    }

    /**
     * 获取分析结果ID.
     *
     * @return AnalysisResultId
     */
    public AnalysisResultId getResultId() {
        return resultId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "AnalysisExecutionCompleted: project " + projectId
                + ", config " + configId
                + ", result " + resultId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalysisExecutionCompletedEvent that = (AnalysisExecutionCompletedEvent) o;
        return Objects.equals(projectId, that.projectId)
                && Objects.equals(configId, that.configId)
                && Objects.equals(resultId, that.resultId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, configId, resultId, occurredOn);
    }

    @Override
    public String toString() {
        return "AnalysisExecutionCompletedEvent{"
                + "projectId=" + projectId
                + ", configId=" + configId
                + ", resultId=" + resultId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
