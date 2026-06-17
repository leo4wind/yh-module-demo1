package com.clinicaltrial.ddd.statistics.application.command;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisResultId;

import java.util.Objects;

/**
 * SaveResultCommand — 保存结果指令.
 * <p>
 * 封装保存或更新分析结果所需的输入参数。
 * 支持更新结果数据和切换收藏状态。
 * </p>
 */
public class SaveResultCommand {

    private final AnalysisProjectId projectId;
    private final AnalysisResultId resultId;
    private final String data;
    private final String resultSummary;
    private final Boolean favorite;

    /**
     * 构造SaveResultCommand.
     *
     * @param projectId     分析项目ID
     * @param resultId      分析结果ID
     * @param data          结果数据（JSON格式，可为null）
     * @param resultSummary 结果摘要（可为null）
     * @param favorite      收藏状态（true为收藏，false为取消收藏，null为不改变）
     */
    public SaveResultCommand(AnalysisProjectId projectId,
                              AnalysisResultId resultId,
                              String data,
                              String resultSummary,
                              Boolean favorite) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.resultId = Objects.requireNonNull(resultId, "resultId must not be null");
        this.data = data;
        this.resultSummary = resultSummary;
        this.favorite = favorite;
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
     * 获取分析结果ID.
     *
     * @return AnalysisResultId
     */
    public AnalysisResultId getResultId() {
        return resultId;
    }

    /**
     * 获取结果数据.
     *
     * @return JSON字符串，可能为null
     */
    public String getData() {
        return data;
    }

    /**
     * 获取结果摘要.
     *
     * @return 摘要文本，可能为null
     */
    public String getResultSummary() {
        return resultSummary;
    }

    /**
     * 获取收藏状态.
     *
     * @return Boolean，可为null
     */
    public Boolean getFavorite() {
        return favorite;
    }
}
