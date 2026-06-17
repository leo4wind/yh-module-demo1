package com.clinicaltrial.ddd.dataexport.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFieldConfigId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.util.Objects;

/**
 * ExportFieldConfig — 导出字段配置实体.
 * <p>
 * 定义导出任务中需要导出的字段及其配置信息。
 * 每个字段配置指定CRF中一个字段的导出方式，包括字段编码、
 * 字段标签、数据来源类型和关联的CRF版本。
 * </p>
 */
public class ExportFieldConfig extends Entity<ExportFieldConfigId> {

    private ExportFieldConfigId id;
    private ExportTaskId exportTaskId;
    private String fieldCode;
    private String fieldLabel;
    private String sourceType;
    private String crfVersionId;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private ExportFieldConfig() {
    }

    /**
     * 创建导出字段配置.
     *
     * @param id           字段配置ID
     * @param exportTaskId 所属导出任务ID
     * @param fieldCode    字段编码
     * @param fieldLabel   字段标签
     * @param sourceType   数据来源类型（如demographics, lab, vital_signs等）
     * @param crfVersionId 关联的CRF版本ID（可为null）
     * @return ExportFieldConfig实例
     */
    public static ExportFieldConfig create(ExportFieldConfigId id,
                                            ExportTaskId exportTaskId,
                                            String fieldCode,
                                            String fieldLabel,
                                            String sourceType,
                                            String crfVersionId) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(exportTaskId, "exportTaskId must not be null");
        Objects.requireNonNull(fieldCode, "fieldCode must not be null");
        Objects.requireNonNull(fieldLabel, "fieldLabel must not be null");

        ExportFieldConfig config = new ExportFieldConfig();
        config.id = id;
        config.exportTaskId = exportTaskId;
        config.fieldCode = fieldCode;
        config.fieldLabel = fieldLabel;
        config.sourceType = sourceType;
        config.crfVersionId = crfVersionId;
        return config;
    }

    @Override
    public ExportFieldConfigId getId() {
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
     * 获取字段编码.
     *
     * @return 字段编码
     */
    public String getFieldCode() {
        return fieldCode;
    }

    /**
     * 获取字段标签.
     *
     * @return 字段标签
     */
    public String getFieldLabel() {
        return fieldLabel;
    }

    /**
     * 获取数据来源类型.
     *
     * @return 来源类型
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * 获取关联的CRF版本ID.
     *
     * @return CRF版本ID，可能为null
     */
    public String getCrfVersionId() {
        return crfVersionId;
    }

    @Override
    public String toString() {
        return "ExportFieldConfig{"
                + "id=" + id
                + ", fieldCode='" + fieldCode + '\''
                + ", fieldLabel='" + fieldLabel + '\''
                + ", sourceType='" + sourceType + '\''
                + '}';
    }
}
