package com.clinicaltrial.ddd.statistics.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisResultId;

import java.util.Date;
import java.util.Objects;

/**
 * AnalysisResult — 分析结果实体.
 * <p>
 * 保存一次统计分析执行的完整结果，包括原始结果JSON数据、
 * 结果摘要、参数配置、收藏状态和创建时间。
 * </p>
 */
public class AnalysisResult extends Entity<AnalysisResultId> {

    private AnalysisResultId id;
    private String name;
    private String method;
    private String data;
    private String resultSummary;
    private String params;
    private boolean isFavorite;
    private Date createTime;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private AnalysisResult() {
    }

    /**
     * 创建分析结果.
     *
     * @param id            结果ID
     * @param name          结果名称
     * @param method        分析方法名称
     * @param data          原始结果数据（JSON格式）
     * @param resultSummary 结果摘要
     * @param params        分析参数（JSON格式）
     * @return AnalysisResult实例
     */
    public static AnalysisResult create(AnalysisResultId id,
                                         String name,
                                         String method,
                                         String data,
                                         String resultSummary,
                                         String params) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(method, "method must not be null");

        AnalysisResult result = new AnalysisResult();
        result.id = id;
        result.name = name;
        result.method = method;
        result.data = data;
        result.resultSummary = resultSummary;
        result.params = params;
        result.isFavorite = false;
        result.createTime = new Date();
        return result;
    }

    /**
     * 切换收藏状态.
     */
    public void toggleFavorite() {
        this.isFavorite = !this.isFavorite;
    }

    /**
     * 标记为收藏.
     */
    public void markAsFavorite() {
        this.isFavorite = true;
    }

    /**
     * 取消收藏.
     */
    public void unmarkAsFavorite() {
        this.isFavorite = false;
    }

    @Override
    public AnalysisResultId getId() {
        return id;
    }

    /**
     * 获取结果名称.
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取分析方法名称.
     *
     * @return 方法名称
     */
    public String getMethod() {
        return method;
    }

    /**
     * 获取原始结果数据（JSON格式）.
     *
     * @return JSON字符串
     */
    public String getData() {
        return data;
    }

    /**
     * 获取结果摘要.
     *
     * @return 摘要文本
     */
    public String getResultSummary() {
        return resultSummary;
    }

    /**
     * 获取分析参数（JSON格式）.
     *
     * @return JSON字符串
     */
    public String getParams() {
        return params;
    }

    /**
     * 判断是否已收藏.
     *
     * @return true 如果已收藏
     */
    public boolean isFavorite() {
        return isFavorite;
    }

    /**
     * 获取创建时间.
     *
     * @return Date
     */
    public Date getCreateTime() {
        return createTime != null ? (Date) createTime.clone() : null;
    }

    @Override
    public String toString() {
        return "AnalysisResult{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", method='" + method + '\''
                + ", isFavorite=" + isFavorite
                + '}';
    }
}
