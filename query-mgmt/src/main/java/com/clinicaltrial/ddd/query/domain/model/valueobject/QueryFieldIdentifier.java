package com.clinicaltrial.ddd.query.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * QueryFieldIdentifier — 质疑字段标识值对象.
 * <p>
 * 用于唯一定位被质疑的CRF字段，包含字段编码、子表ID和字段类型三个维度。
 * 通过equals/hashCode支持重复质疑检测：同一CRF评估下，相同标识的字段
 * 不能有多个未关闭的质疑。
 * </p>
 */
public class QueryFieldIdentifier implements ValueObject {

    private final String fieldCode;
    private final String subTableId;
    private final String fieldType;

    /**
     * 构造QueryFieldIdentifier.
     *
     * @param fieldCode  字段编码，如"btbm00"
     * @param subTableId 子表ID，非子表字段可为null或空字符串
     * @param fieldType  字段类型，如"text"、"number"、"date"等
     */
    public QueryFieldIdentifier(String fieldCode, String subTableId, String fieldType) {
        if (fieldCode == null || fieldCode.trim().isEmpty()) {
            throw new IllegalArgumentException("fieldCode must not be null or empty");
        }
        this.fieldCode = fieldCode;
        this.subTableId = subTableId != null ? subTableId : "";
        this.fieldType = fieldType != null ? fieldType : "";
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
     * 获取子表ID.
     *
     * @return 子表ID，可能为空字符串
     */
    public String getSubTableId() {
        return subTableId;
    }

    /**
     * 获取字段类型.
     *
     * @return 字段类型，可能为空字符串
     */
    public String getFieldType() {
        return fieldType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryFieldIdentifier that = (QueryFieldIdentifier) o;
        return Objects.equals(fieldCode, that.fieldCode)
                && Objects.equals(subTableId, that.subTableId)
                && Objects.equals(fieldType, that.fieldType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldCode, subTableId, fieldType);
    }

    @Override
    public String toString() {
        return "QueryFieldIdentifier{"
                + "fieldCode='" + fieldCode + '\''
                + ", subTableId='" + subTableId + '\''
                + ", fieldType='" + fieldType + '\''
                + '}';
    }
}
