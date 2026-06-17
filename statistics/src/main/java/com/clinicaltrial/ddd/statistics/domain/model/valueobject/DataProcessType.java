package com.clinicaltrial.ddd.statistics.domain.model.valueobject;

/**
 * DataProcessType — 数据处理类型枚举.
 * <p>
 * 定义数据分析前可应用的数据处理操作类型：
 * <ul>
 *   <li>CATEGORIZE — 分类化（将连续变量转换为分类变量）</li>
 *   <li>MERGE — 合并（将多个变量或值合并为一个）</li>
 *   <li>SPLIT — 拆分（将一个变量拆分为多个）</li>
 *   <li>FILL_MISSING — 缺失值填充</li>
 *   <li>NORMALIZE — 归一化/标准化</li>
 * </ul>
 * </p>
 */
public enum DataProcessType {

    /**
     * 分类化 — 将连续变量按阈值转换为分类变量.
     */
    CATEGORIZE("分类化"),

    /**
     * 合并 — 合并多个变量或值.
     */
    MERGE("合并"),

    /**
     * 拆分 — 将一个变量拆分为多个变量.
     */
    SPLIT("拆分"),

    /**
     * 缺失值填充 — 使用统计量或指定值填充缺失数据.
     */
    FILL_MISSING("缺失值填充"),

    /**
     * 归一化 — 将数值缩放到特定范围.
     */
    NORMALIZE("归一化");

    private final String description;

    /**
     * 构造DataProcessType.
     *
     * @param description 类型描述
     */
    DataProcessType(String description) {
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
