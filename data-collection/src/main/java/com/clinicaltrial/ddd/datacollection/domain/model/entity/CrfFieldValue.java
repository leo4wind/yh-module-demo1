package com.clinicaltrial.ddd.datacollection.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfFieldValueId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SnapshotValue;

import java.util.Date;
import java.util.Objects;

/**
 * CrfFieldValue — CRF字段值实体.
 * <p>
 * 属于CrfAssessment聚合内的实体，表示CRF评估中单个字段的填写值。
 * 每个字段值包含字段定义编码、标签、值和数据类型等信息。
 * </p>
 */
public class CrfFieldValue extends Entity<CrfFieldValueId> {

    private final CrfFieldValueId id;
    private final CrfAssessmentId assessmentId;
    private String fieldCode;
    private String fieldLabel;
    private String fieldValue;
    private String fieldValueText;
    private String dataUnit;
    private String fieldType;
    private String subTableId;
    private Integer sortNumber;

    /**
     * 构造CrfFieldValue.
     *
     * @param id             字段值ID
     * @param assessmentId   所属CRF评估ID
     * @param fieldCode      字段编码（如btbm00）
     * @param fieldLabel     字段标签（如btms00）
     * @param fieldValue     字段值（编码，如dabm00 code）
     * @param fieldValueText 字段值显示文本（如dams00 display）
     * @param dataUnit       数据单位（如daqtms）
     * @param fieldType      字段类型（如dalx00）
     * @param subTableId     子表ID（如tcid00）
     * @param sortNumber     排序号
     */
    public CrfFieldValue(CrfFieldValueId id, CrfAssessmentId assessmentId,
                         String fieldCode, String fieldLabel,
                         String fieldValue, String fieldValueText,
                         String dataUnit, String fieldType,
                         String subTableId, Integer sortNumber) {
        this.id = id;
        this.assessmentId = assessmentId;
        this.fieldCode = fieldCode;
        this.fieldLabel = fieldLabel;
        this.fieldValue = fieldValue;
        this.fieldValueText = fieldValueText;
        this.dataUnit = dataUnit;
        this.fieldType = fieldType;
        this.subTableId = subTableId;
        this.sortNumber = sortNumber;
    }

    @Override
    public CrfFieldValueId getId() {
        return id;
    }

    /**
     * 获取所属CRF评估ID.
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
     * 获取排序号.
     *
     * @return 排序号
     */
    public Integer getSortNumber() {
        return sortNumber;
    }

    /**
     * 更新字段值.
     *
     * @param newValue     新值（编码）
     * @param newValueText 新值显示文本
     */
    public void updateValue(String newValue, String newValueText) {
        this.fieldValue = newValue;
        this.fieldValueText = newValueText;
    }

    /**
     * 判断是否与指定字段编码匹配.
     *
     * @param fieldCode 待匹配的字段编码
     * @return true 如果匹配
     */
    public boolean matches(String fieldCode) {
        return Objects.equals(this.fieldCode, fieldCode);
    }

    /**
     * 创建当前字段值的快照，用于审计追踪.
     *
     * @return 快照值对象
     */
    public SnapshotValue snapshot() {
        return new SnapshotValue(
                this.fieldCode,
                this.fieldLabel,
                this.fieldValue,
                this.fieldValueText,
                new Date()
        );
    }

    @Override
    public String toString() {
        return "CrfFieldValue{"
                + "id=" + id
                + ", assessmentId=" + assessmentId
                + ", fieldCode='" + fieldCode + '\''
                + ", fieldLabel='" + fieldLabel + '\''
                + ", fieldValue='" + fieldValue + '\''
                + ", fieldValueText='" + fieldValueText + '\''
                + ", dataUnit='" + dataUnit + '\''
                + ", fieldType='" + fieldType + '\''
                + ", subTableId='" + subTableId + '\''
                + ", sortNumber=" + sortNumber
                + '}';
    }
}
