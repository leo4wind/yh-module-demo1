package com.clinicaltrial.ddd.dataexport.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportExecutionLogId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.util.Date;
import java.util.Objects;

/**
 * ExportExecutionLog — 导出执行日志实体.
 * <p>
 * 记录每次导出执行的详细信息，包括执行时间范围、
 * 执行状态、导出记录数和生成的文件名。
 * 一个导出任务可能包含多次执行日志（如重试）。
 * </p>
 */
public class ExportExecutionLog extends Entity<ExportExecutionLogId> {

    /**
     * 执行状态枚举.
     */
    public enum ExecutionStatus {
        RUNNING("执行中"),
        SUCCESS("成功"),
        FAILED("失败");

        private final String description;

        ExecutionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private ExportExecutionLogId id;
    private ExportTaskId exportTaskId;
    private Date startTime;
    private Date endTime;
    private ExecutionStatus status;
    private Integer recordCount;
    private String fileName;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private ExportExecutionLog() {
    }

    /**
     * 创建导出执行日志（初始状态为RUNNING）.
     *
     * @param id           执行日志ID
     * @param exportTaskId 所属导出任务ID
     * @return ExportExecutionLog实例
     */
    public static ExportExecutionLog start(ExportExecutionLogId id, ExportTaskId exportTaskId) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(exportTaskId, "exportTaskId must not be null");

        ExportExecutionLog log = new ExportExecutionLog();
        log.id = id;
        log.exportTaskId = exportTaskId;
        log.startTime = new Date();
        log.status = ExecutionStatus.RUNNING;
        return log;
    }

    /**
     * 标记执行为成功.
     *
     * @param recordCount 导出的记录数
     * @param fileName    生成的文件名
     */
    public void markCompleted(Integer recordCount, String fileName) {
        this.endTime = new Date();
        this.status = ExecutionStatus.SUCCESS;
        this.recordCount = recordCount;
        this.fileName = fileName;
    }

    /**
     * 标记执行为失败.
     */
    public void markFailed() {
        this.endTime = new Date();
        this.status = ExecutionStatus.FAILED;
    }

    @Override
    public ExportExecutionLogId getId() {
        return id;
    }

    /**
     * 获取所属导出任务ID.
     *
     * @return ExportTaskId
     */
    public ExportTaskId getExportTaskId() {
        return exportTaskId;
    }

    /**
     * 获取执行开始时间.
     *
     * @return Date
     */
    public Date getStartTime() {
        return startTime != null ? (Date) startTime.clone() : null;
    }

    /**
     * 获取执行结束时间.
     *
     * @return Date，可能为null（未结束）
     */
    public Date getEndTime() {
        return endTime != null ? (Date) endTime.clone() : null;
    }

    /**
     * 获取执行状态.
     *
     * @return ExecutionStatus
     */
    public ExecutionStatus getStatus() {
        return status;
    }

    /**
     * 获取导出的记录数.
     *
     * @return 记录数
     */
    public Integer getRecordCount() {
        return recordCount;
    }

    /**
     * 获取生成的文件名.
     *
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "ExportExecutionLog{"
                + "id=" + id
                + ", status=" + status
                + ", recordCount=" + recordCount
                + ", fileName='" + fileName + '\''
                + '}';
    }
}
