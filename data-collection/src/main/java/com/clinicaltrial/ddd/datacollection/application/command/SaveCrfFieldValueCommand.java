package com.clinicaltrial.ddd.datacollection.application.command;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;

import java.io.Serializable;
import java.util.Objects;

/**
 * SaveCrfFieldValueCommand — 保存CRF字段值命令.
 * <p>
 * 封装保存/更新CRF评估中某个字段值的所有输入参数。
 * 由应用层接收并传递给CrfFillingApplicationService处理。
 * </p>
 */
public class SaveCrfFieldValueCommand implements Serializable {

    private final CrfAssessmentId assessmentId;
    private final String fieldCode;
    private final String fieldLabel;
    private final String fieldValue;
    private final String fieldValueText;
    private final String dataUnit;
    private final String fieldType;
    private final String subTableId;
    private final Long userId;

    /**
     * 使用全参构造函数.
     *
     * @param assessmentId  CRF评估ID
     * @param fieldCode     字段编码（如btbm00）
     * @param fieldLabel    字段标签（如btms00）
     * @param fieldValue    字段值（编码，如dabm00 code）
     * @param fieldValueText 字段值显示文本（如dams00 display）
     * @param dataUnit      数据单位（如daqtms）
     * @param fieldType     字段类型（如dalx00）
     * @param subTableId    子表ID（如tcid00）
     * @param userId        操作用户ID
     */
    public SaveCrfFieldValueCommand(CrfAssessmentId assessmentId, String fieldCode,
                                     String fieldLabel, String fieldValue,
                                     String fieldValueText, String dataUnit,
                                     String fieldType, String subTableId,
                                     Long userId) {
        this.assessmentId = assessmentId;
        this.fieldCode = fieldCode;
        this.fieldLabel = fieldLabel;
        this.fieldValue = fieldValue;
        this.fieldValueText = fieldValueText;
        this.dataUnit = dataUnit;
        this.fieldType = fieldType;
        this.subTableId = subTableId;
        this.userId = userId;
    }

    /**
     * 获取CRF评估ID.
     *
     * @return CrfAssessmentId
     */
    public CrfAssessmentId getAssessmentId() {
        return assessmentId;
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
     * 获取字段值（编码）.
     *
     * @return 字段编码值
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * 获取字段值显示文本.
     *
     * @return 显示文本
     */
    public String getFieldValueText() {
        return fieldValueText;
    }

    /**
     * 获取数据单位.
     *
     * @return 数据单位
     */
    public String getDataUnit() {
        return dataUnit;
    }

    /**
     * 获取字段类型.
     *
     * @return 字段类型编码
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * 获取子表ID.
     *
     * @return 子表ID
     */
    public String getSubTableId() {
        return subTableId;
    }

    /**
     * 获取操作用户ID.
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SaveCrfFieldValueCommand that = (SaveCrfFieldValueCommand) o;
        return Objects.equals(assessmentId, that.assessmentId)
                && Objects.equals(fieldCode, that.fieldCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentId, fieldCode);
    }

    @Override
    public String toString() {
        return "SaveCrfFieldValueCommand{"
                + "assessmentId=" + assessmentId
                + ", fieldCode='" + fieldCode + '\''
                + ", fieldValue='" + fieldValue + '\''
                + '}';
    }
}
