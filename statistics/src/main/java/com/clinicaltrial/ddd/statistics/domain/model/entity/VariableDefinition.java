package com.clinicaltrial.ddd.statistics.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableDefinitionId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableType;

import java.util.Objects;

/**
 * VariableDefinition — 变量定义实体.
 * <p>
 * 定义分析项目中使用的变量，包括变量名称、标签、类型和来源。
 * 变量可以是直接从数据源导入的字段，也可以是通过表达式派生的计算变量。
 * </p>
 */
public class VariableDefinition extends Entity<VariableDefinitionId> {

    private VariableDefinitionId id;
    private String name;
    private String label;
    private VariableType variableType;
    private String sourceField;
    private boolean derived;
    private String expression;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private VariableDefinition() {
    }

    /**
     * 创建变量定义.
     *
     * @param id           变量定义ID
     * @param name         变量名称（编码）
     * @param label        变量标签（显示名）
     * @param variableType 变量类型
     * @param sourceField  源数据字段名
     * @return VariableDefinition实例
     */
    public static VariableDefinition create(VariableDefinitionId id,
                                             String name,
                                             String label,
                                             VariableType variableType,
                                             String sourceField) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(variableType, "variableType must not be null");
        Objects.requireNonNull(sourceField, "sourceField must not be null");

        VariableDefinition vd = new VariableDefinition();
        vd.id = id;
        vd.name = name;
        vd.label = label;
        vd.variableType = variableType;
        vd.sourceField = sourceField;
        vd.derived = false;
        return vd;
    }

    /**
     * 创建派生变量定义（由表达式计算得来）.
     *
     * @param id           变量定义ID
     * @param name         变量名称（编码）
     * @param label        变量标签（显示名）
     * @param variableType 变量类型
     * @param expression   计算表达式
     * @return VariableDefinition实例
     */
    public static VariableDefinition createDerived(VariableDefinitionId id,
                                                    String name,
                                                    String label,
                                                    VariableType variableType,
                                                    String expression) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(variableType, "variableType must not be null");
        Objects.requireNonNull(expression, "expression must not be null");

        VariableDefinition vd = new VariableDefinition();
        vd.id = id;
        vd.name = name;
        vd.label = label;
        vd.variableType = variableType;
        vd.sourceField = name;
        vd.derived = true;
        vd.expression = expression;
        return vd;
    }

    @Override
    public VariableDefinitionId getId() {
        return id;
    }

    /**
     * 获取变量名称.
     *
     * @return 变量名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取变量标签.
     *
     * @return 变量标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 获取变量类型.
     *
     * @return VariableType
     */
    public VariableType getVariableType() {
        return variableType;
    }

    /**
     * 获取源数据字段名.
     *
     * @return 源字段名
     */
    public String getSourceField() {
        return sourceField;
    }

    /**
     * 判断是否为派生变量.
     *
     * @return true 如果是通过表达式计算得来
     */
    public boolean isDerived() {
        return derived;
    }

    /**
     * 获取计算表达式（仅派生变量有值）.
     *
     * @return 表达式，非派生变量返回null
     */
    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "VariableDefinition{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", label='" + label + '\''
                + ", variableType=" + variableType
                + ", derived=" + derived
                + '}';
    }
}
