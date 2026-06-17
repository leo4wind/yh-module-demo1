package com.clinicaltrial.ddd.datacollection.domain.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * CrfField — CRF模板字段定义（值对象）.
 * <p>
 * 表示CRF模板中一个字段的元数据定义，来自BC1（试验配置）的CRF模板。
 * 在数据采集上下文中用于：
 * <ul>
 *   <li>计算CRF评估完整性时判断字段是否为必填</li>
 *   <li>判断字段是否被隐藏或有条件隐藏（跳过完整性计算）</li>
 * </ul>
 * 这是一个不可变的值对象，由应用层从外部获取后传入领域层使用。
 * </p>
 */
public class CrfField implements Serializable {

    private final String fieldCode;
    private final boolean required;
    private final boolean hidden;
    private final boolean conditionallyHidden;

    /**
     * 构造CrfField.
     *
     * @param fieldCode          字段编码
     * @param required           是否必填
     * @param hidden             是否隐藏
     * @param conditionallyHidden 是否有条件隐藏
     */
    public CrfField(String fieldCode, boolean required, boolean hidden, boolean conditionallyHidden) {
        this.fieldCode = fieldCode;
        this.required = required;
        this.hidden = hidden;
        this.conditionallyHidden = conditionallyHidden;
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
     * 是否必填字段.
     *
     * @return true 如果必填
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * 是否隐藏字段.
     *
     * @return true 如果隐藏
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * 是否有条件隐藏（根据其他字段值决定是否显示）.
     *
     * @return true 如果有条件隐藏
     */
    public boolean isConditionallyHidden() {
        return conditionallyHidden;
    }

    /**
     * 判断是否应在完整性计算中被考虑.
     * <p>
     * 隐藏或有条件隐藏的字段不计入完整性计算。
     * </p>
     *
     * @return true 如果该字段应参与完整性计算
     */
    public boolean isCountable() {
        return required && !hidden && !conditionallyHidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrfField crfField = (CrfField) o;
        return Objects.equals(fieldCode, crfField.fieldCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldCode);
    }

    @Override
    public String toString() {
        return "CrfField{" + "fieldCode='" + fieldCode + '\''
                + ", required=" + required
                + ", hidden=" + hidden
                + ", conditionallyHidden=" + conditionallyHidden + '}';
    }
}
