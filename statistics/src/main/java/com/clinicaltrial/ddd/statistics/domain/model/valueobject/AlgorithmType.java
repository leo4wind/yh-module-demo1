package com.clinicaltrial.ddd.statistics.domain.model.valueobject;

/**
 * AlgorithmType — 统计算法类型枚举.
 * <p>
 * 定义支持的统计分析算法类型：
 * <ul>
 *   <li>CHI_SQUARE — 卡方检验（分类变量关联性检验）</li>
 *   <li>T_TEST — t检验（两组均值比较）</li>
 *   <li>ANOVA — 方差分析（多组均值比较）</li>
 *   <li>REGRESSION — 回归分析（因变量与自变量的关系建模）</li>
 *   <li>DESCRIPTIVE — 描述性统计（均值、标准差、频数等）</li>
 * </ul>
 * </p>
 */
public enum AlgorithmType {

    /**
     * 卡方检验 — 用于分类变量的关联性检验.
     */
    CHI_SQUARE("卡方检验"),

    /**
     * t检验 — 用于两组均值的比较.
     */
    T_TEST("t检验"),

    /**
     * 方差分析 — 用于多组均值的比较.
     */
    ANOVA("方差分析"),

    /**
     * 回归分析 — 因变量与自变量的关系建模.
     */
    REGRESSION("回归分析"),

    /**
     * 描述性统计 — 均值、标准差、频数、分位数等.
     */
    DESCRIPTIVE("描述性统计");

    private final String description;

    /**
     * 构造AlgorithmType.
     *
     * @param description 算法描述
     */
    AlgorithmType(String description) {
        this.description = description;
    }

    /**
     * 获取算法描述.
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
