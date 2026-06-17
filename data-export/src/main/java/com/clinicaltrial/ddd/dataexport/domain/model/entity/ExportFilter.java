package com.clinicaltrial.ddd.dataexport.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFilterId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.util.Objects;

/**
 * ExportFilter — 导出筛选条件实体.
 * <p>
 * 定义导出任务的数据筛选规则，用于过滤需要导出的数据记录。
 * 支持字段比较操作和逻辑组合，可构建复合筛选条件。
 * </p>
 */
public class ExportFilter extends Entity<ExportFilterId> {

    /**
     * 比较操作符枚举.
     */
    public enum Operator {
        EQ("等于"),
        NEQ("不等于"),
        GT("大于"),
        LT("小于"),
        IN("包含于"),
        LIKE("模糊匹配");

        private final String description;

        Operator(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 逻辑操作符枚举.
     */
    public enum LogicOperator {
        AND("与"),
        OR("或");

        private final String description;

        LogicOperator(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private ExportFilterId id;
    private ExportTaskId exportTaskId;
    private String fieldCode;
    private Operator operator;
    private String filterValue;
    private LogicOperator logicOperator;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private ExportFilter() {
    }

    /**
     * 创建导出筛选条件.
     *
     * @param id            筛选条件ID
     * @param exportTaskId  所属导出任务ID
     * @param fieldCode     筛选字段编码
     * @param operator      比较操作符
     * @param filterValue   筛选值
     * @param logicOperator 逻辑操作符（与下一个条件的组合方式）
     * @return ExportFilter实例
     */
    public static ExportFilter create(ExportFilterId id,
                                       ExportTaskId exportTaskId,
                                       String fieldCode,
                                       Operator operator,
                                       String filterValue,
                                       LogicOperator logicOperator) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(exportTaskId, "exportTaskId must not be null");
        Objects.requireNonNull(fieldCode, "fieldCode must not be null");
        Objects.requireNonNull(operator, "operator must not be null");
        Objects.requireNonNull(filterValue, "filterValue must not be null");

        ExportFilter filter = new ExportFilter();
        filter.id = id;
        filter.exportTaskId = exportTaskId;
        filter.fieldCode = fieldCode;
        filter.operator = operator;
        filter.filterValue = filterValue;
        filter.logicOperator = logicOperator;
        return filter;
    }

    @Override
    public ExportFilterId getId() {
        return id;
    }

    /**
     * 获取所属导出任务ID.
     *
     * @return ExportTaskId
     */
    public ExportTaskId getExportTaskId() {
        return exportTaskId;
    }

    /**
     * 获取筛选字段编码.
     *
     * @return 字段编码
     */
    public String getFieldCode() {
        return fieldCode;
    }

    /**
     * 获取比较操作符.
     *
     * @return Operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * 获取筛选值.
     *
     * @return 筛选值
     */
    public String getFilterValue() {
        return filterValue;
    }

    /**
     * 获取逻辑操作符.
     *
     * @return LogicOperator
     */
    public LogicOperator getLogicOperator() {
        return logicOperator;
    }

    @Override
    public String toString() {
        return "ExportFilter{"
                + "id=" + id
                + ", fieldCode='" + fieldCode + '\''
                + ", operator=" + operator
                + ", filterValue='" + filterValue + '\''
                + ", logicOperator=" + logicOperator
                + '}';
    }
}
