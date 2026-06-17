package com.clinicaltrial.ddd.interfaces.dto.response;

import java.util.Date;

/**
 * 导出任务响应.
 */
public class ExportTaskResponse {

    private Long id;
    private String taskName;
    private Long projectId;
    private Long stageId;
    private Long crfVersionId;
    private String status;
    private String fileFormat;
    private String auditUserId;
    private Date auditTime;
    private String auditMessage;
    private String fileUrl;
    private String fileName;
    private Integer downloadCount;
    private Integer failCount;
    private String failMessage;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public Long getCrfVersionId() { return crfVersionId; }
    public void setCrfVersionId(Long crfVersionId) { this.crfVersionId = crfVersionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
    public String getAuditUserId() { return auditUserId; }
    public void setAuditUserId(String auditUserId) { this.auditUserId = auditUserId; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
    public String getAuditMessage() { return auditMessage; }
    public void setAuditMessage(String auditMessage) { this.auditMessage = auditMessage; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
    public Integer getFailCount() { return failCount; }
    public void setFailCount(Integer failCount) { this.failCount = failCount; }
    public String getFailMessage() { return failMessage; }
    public void setFailMessage(String failMessage) { this.failMessage = failMessage; }
}
