package com.clinicaltrial.ddd.statistics.domain.model.valueobject;

/**
 * VariableType — 变量类型枚举.
 * <p>
 * 定义分析数据中的变量类型分类：
 * <ul>
 *   <li>NUMERIC — 数值型（如年龄、身高、体重）</li>
 *   <li>CATEGORICAL — 分类变量（如性别、种族、治疗组）</li>
 *   <li>ORDINAL — 有序分类（如疾病分级、评分等级）</li>
 *   <li>DATE — 日期型（如出生日期、访视日期）</li>
 *   <li>TEXT — 文本型（如备注、描述）</li>
 * </ul>
 * </p>
 */
public enum VariableType {

    /**
     * 数值型 — 连续数值，适用于均值、标准差等统计.
     */
    NUMERIC("数值型"),

    /**
     * 分类变量 — 无序类别（如性别、治疗组）.
     */
    CATEGORICAL("分类变量"),

    /**
     * 有序分类 — 有顺序的类别（如分级、分期）.
     */
    ORDINAL("有序分类"),

    /**
     * 日期型 — 日期或时间.
     */
    DATE("日期型"),

    /**
     * 文本型 — 自由文本.
     */
    TEXT("文本型");

    private final String description;

    /**
     * 构造VariableType.
     *
     * @param description 类型描述
     */
    VariableType(String description) {
        this.description = description;
    }

    /**
     * 获取类型描述.
     *
     * @return 中文描述
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + "(" + description + ")";
    }
}
