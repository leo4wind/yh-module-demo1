package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Date;
import java.util.Objects;

/**
 * SnapshotValue — 字段值快照值对象.
 * <p>
 * 用于审计追踪，记录CrfFieldValue在某个时刻的完整快照。
 * 包含字段编码、标签、值以及快照时间戳。
 * </p>
 */
public class SnapshotValue implements ValueObject {

    private final String fieldCode;
    private final String fieldLabel;
    private final String fieldValue;
    private final String fieldValueText;
    private final Date snapshotAt;

    /**
     * 构造SnapshotValue.
     *
     * @param fieldCode      字段编码
     * @param fieldLabel     字段标签
     * @param fieldValue     字段值（编码）
     * @param fieldValueText 字段值（显示文本）
     * @param snapshotAt     快照时间
     */
    public SnapshotValue(String fieldCode, String fieldLabel,
                         String fieldValue, String fieldValueText, Date snapshotAt) {
        this.fieldCode = fieldCode;
        this.fieldLabel = fieldLabel;
        this.fieldValue = fieldValue;
        this.fieldValueText = fieldValueText;
        this.snapshotAt = snapshotAt != null ? (Date) snapshotAt.clone() : null;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public String getFieldValueText() {
        return fieldValueText;
    }

    public Date getSnapshotAt() {
        return snapshotAt != null ? (Date) snapshotAt.clone() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SnapshotValue that = (SnapshotValue) o;
        return Objects.equals(fieldCode, that.fieldCode)
                && Objects.equals(fieldLabel, that.fieldLabel)
                && Objects.equals(fieldValue, that.fieldValue)
                && Objects.equals(fieldValueText, that.fieldValueText)
                && Objects.equals(snapshotAt, that.snapshotAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldCode, fieldLabel, fieldValue, fieldValueText, snapshotAt);
    }

    @Override
    public String toString() {
        return "SnapshotValue{" + "fieldCode='" + fieldCode + '\''
                + ", fieldLabel='" + fieldLabel + '\''
                + ", fieldValue='" + fieldValue + '\''
                + ", fieldValueText='" + fieldValueText + '\''
                + ", snapshotAt=" + snapshotAt + '}';
    }
}
