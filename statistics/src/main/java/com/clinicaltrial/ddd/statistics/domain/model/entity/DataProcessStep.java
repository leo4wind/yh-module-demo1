package com.clinicaltrial.ddd.statistics.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessStepId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessType;

import java.util.Objects;

/**
 * DataProcessStep — 数据处理步骤实体.
 * <p>
 * 定义数据分析前对数据进行预处理的一个步骤。
 * 多个处理步骤按顺序执行，形成一个数据处理流水线。
 * 每个步骤包含处理类型、配置参数和执行顺序。
 * </p>
 */
public class DataProcessStep extends Entity<DataProcessStepId> {

    private DataProcessStepId id;
    private DataProcessType processType;
    private String configJson;
    private Integer sortOrder;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private DataProcessStep() {
    }

    /**
     * 创建数据处理步骤.
     *
     * @param id          步骤ID
     * @param processType 处理类型
     * @param configJson  配置参数（JSON格式）
     * @param sortOrder   执行顺序
     * @return DataProcessStep实例
     */
    public static DataProcessStep create(DataProcessStepId id,
                                          DataProcessType processType,
                                          String configJson,
                                          Integer sortOrder) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(processType, "processType must not be null");
        Objects.requireNonNull(sortOrder, "sortOrder must not be null");

        DataProcessStep step = new DataProcessStep();
        step.id = id;
        step.processType = processType;
        step.configJson = configJson;
        step.sortOrder = sortOrder;
        return step;
    }

    @Override
    public DataProcessStepId getId() {
        return id;
    }

    /**
     * 获取处理类型.
     *
     * @return DataProcessType
     */
    public DataProcessType getProcessType() {
        return processType;
    }

    /**
     * 获取配置参数（JSON格式）.
     *
     * @return JSON配置字符串
     */
    public String getConfigJson() {
        return configJson;
    }

    /**
     * 获取执行顺序.
     *
     * @return 排序值
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        return "DataProcessStep{"
                + "id=" + id
                + ", processType=" + processType
                + ", sortOrder=" + sortOrder
                + '}';
    }
}
