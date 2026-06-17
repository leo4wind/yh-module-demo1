package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 创建导出任务请求.
 */
public class CreateExportTaskRequest {

    private String taskName;
    private Long projectId;
    private Long stageId;
    private Long crfVersionId;
    private String fileFormat;  // FileFormat name: CSV / EXCEL / PDF / WORD / JSON

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public Long getCrfVersionId() { return crfVersionId; }
    public void setCrfVersionId(Long crfVersionId) { this.crfVersionId = crfVersionId; }
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
}
