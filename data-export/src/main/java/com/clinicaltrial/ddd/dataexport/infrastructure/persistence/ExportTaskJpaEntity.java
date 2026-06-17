package com.clinicaltrial.ddd.dataexport.infrastructure.persistence;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportStatus;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.FileFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JPA entity mirroring the ExportTask aggregate root.
 * Maps to the rd_export_task table.
 */
@Entity
@Table(name = "rd_export_task")
public class ExportTaskJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "task_name", length = 200)
    private String taskName;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "stage_id")
    private Long stageId;

    @Column(name = "crf_version_id")
    private Long crfVersionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private ExportStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_format", length = 20)
    private FileFormat fileFormat;

    @Column(name = "audit_user_id", length = 100)
    private String auditUserId;

    @Column(name = "audit_time")
    private Date auditTime;

    @Column(name = "audit_message", length = 1000)
    private String auditMessage;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "file_name", length = 500)
    private String fileName;

    @Column(name = "download_count")
    private Integer downloadCount;

    @Column(name = "fail_count")
    private Integer failCount;

    @Column(name = "fail_message", length = 2000)
    private String failMessage;

    @OneToMany(mappedBy = "taskId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExportFieldConfigJpaEntity> fieldConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "taskId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExportFilterJpaEntity> filters = new ArrayList<>();

    @OneToMany(mappedBy = "taskId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExportExecutionLogJpaEntity> executionLogs = new ArrayList<>();

    public ExportTaskJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public Long getCrfVersionId() {
        return crfVersionId;
    }

    public void setCrfVersionId(Long crfVersionId) {
        this.crfVersionId = crfVersionId;
    }

    public ExportStatus getStatus() {
        return status;
    }

    public void setStatus(ExportStatus status) {
        this.status = status;
    }

    public FileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public List<ExportFieldConfigJpaEntity> getFieldConfigs() {
        return fieldConfigs;
    }

    public void setFieldConfigs(List<ExportFieldConfigJpaEntity> fieldConfigs) {
        this.fieldConfigs = fieldConfigs;
    }

    public List<ExportFilterJpaEntity> getFilters() {
        return filters;
    }

    public void setFilters(List<ExportFilterJpaEntity> filters) {
        this.filters = filters;
    }

    public List<ExportExecutionLogJpaEntity> getExecutionLogs() {
        return executionLogs;
    }

    public void setExecutionLogs(List<ExportExecutionLogJpaEntity> executionLogs) {
        this.executionLogs = executionLogs;
    }
}
