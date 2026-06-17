package com.clinicaltrial.ddd.statistics.application.command;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessStepId;

import java.util.List;
import java.util.Objects;

/**
 * ProcessDataCommand — 处理数据指令.
 * <p>
 * 封装对分析项目数据执行预处理操作的输入参数。
 * 可以指定执行全部处理步骤或仅执行指定的步骤。
 * </p>
 */
public class ProcessDataCommand {

    private final AnalysisProjectId projectId;
    private final List<DataProcessStepId> stepIds;

    /**
     * 构造ProcessDataCommand.
     *
     * @param projectId 分析项目ID
     * @param stepIds   要执行的处理步骤ID列表（null或空列表表示执行全部）
     */
    public ProcessDataCommand(AnalysisProjectId projectId, List<DataProcessStepId> stepIds) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.stepIds = stepIds;
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
     * 获取要执行的步骤ID列表.
     *
     * @return 步骤ID列表，可能为null
     */
    public List<DataProcessStepId> getStepIds() {
        return stepIds;
    }

    /**
     * 判断是否执行全部步骤.
     *
     * @return true 如果stepIds为null或空
     */
    public boolean isExecuteAll() {
        return stepIds == null || stepIds.isEmpty();
    }
}
