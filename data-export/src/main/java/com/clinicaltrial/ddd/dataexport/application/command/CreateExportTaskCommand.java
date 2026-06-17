package com.clinicaltrial.ddd.dataexport.application.command;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.FileFormat;

import java.util.Objects;

/**
 * CreateExportTaskCommand — 创建导出任务指令.
 * <p>
 * 封装创建导出任务所需的全部输入参数。
 * 由应用层服务处理，转换为领域操作。
 * </p>
 */
public class CreateExportTaskCommand {

    private final String taskName;
    private final String projectId;
    private final String stageId;
    private final String crfVersionId;
    private final FileFormat fileFormat;

    /**
     * 构造CreateExportTaskCommand.
     *
     * @param taskName     任务名称
     * @param projectId    项目ID
     * @param stageId      阶段ID（可为null）
     * @param crfVersionId CRF版本ID（可为null）
     * @param fileFormat   导出文件格式
     */
    public CreateExportTaskCommand(String taskName, String projectId,
                                    String stageId, String crfVersionId,
                                    FileFormat fileFormat) {
        this.taskName = Objects.requireNonNull(taskName, "taskName must not be null");
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.stageId = stageId;
        this.crfVersionId = crfVersionId;
        this.fileFormat = Objects.requireNonNull(fileFormat, "fileFormat must not be null");
    }

    /**
     * 获取任务名称.
     *
     * @return 任务名称
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * 获取项目ID.
     *
     * @return 项目ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 获取阶段ID.
     *
     * @return 阶段ID，可能为null
     */
    public String getStageId() {
        return stageId;
    }

    /**
     * 获取CRF版本ID.
     *
     * @return CRF版本ID，可能为null
     */
    public String getCrfVersionId() {
        return crfVersionId;
    }

    /**
     * 获取导出文件格式.
     *
     * @return FileFormat
     */
    public FileFormat getFileFormat() {
        return fileFormat;
    }
}
