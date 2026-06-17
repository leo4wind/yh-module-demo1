package com.clinicaltrial.ddd.statistics.application.command;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ImportDataCommand — 导入数据指令.
 * <p>
 * 封装向分析项目导入原始数据所需的输入参数。
 * 数据将以列表形式提供，每行为字段名到值的映射。
 * </p>
 */
public class ImportDataCommand {

    private final AnalysisProjectId projectId;
    private final List<Map<String, Object>> rawData;

    /**
     * 构造ImportDataCommand.
     *
     * @param projectId 分析项目ID
     * @param rawData   原始数据（列表形式，每行为字段名-值的映射）
     */
    public ImportDataCommand(AnalysisProjectId projectId, List<Map<String, Object>> rawData) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.rawData = Objects.requireNonNull(rawData, "rawData must not be null");
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
     * 获取原始数据.
     *
     * @return 数据列表
     */
    public List<Map<String, Object>> getRawData() {
        return rawData;
    }
}
