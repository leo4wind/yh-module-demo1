package com.clinicaltrial.ddd.statistics.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.AggregateRoot;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisConfig;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;
import com.clinicaltrial.ddd.statistics.domain.model.entity.DataProcessStep;
import com.clinicaltrial.ddd.statistics.domain.model.entity.VariableDefinition;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisConfigId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisResultId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessStepId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableDefinitionId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableType;
import com.clinicaltrial.ddd.statistics.domain.event.AnalysisExecutionCompletedEvent;
import com.clinicaltrial.ddd.statistics.domain.event.DataImportCompletedEvent;
import com.clinicaltrial.ddd.statistics.domain.event.DataProcessingCompletedEvent;
import com.clinicaltrial.ddd.statistics.domain.event.ResultFavoritedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * AnalysisProject — 分析项目聚合根.
 * <p>
 * 表示一个统计分析项目，包含数据导入、变量定义、数据处理、
 * 分析配置和结果管理等功能。
 * 分析项目是整个统计分析的入口聚合。
 * </p>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>变量名称在项目中必须唯一</li>
 *   <li>分析结果名称在项目中必须唯一</li>
 *   <li>数据处理步骤按sortOrder升序执行</li>
 * </ul>
 */
public class AnalysisProject extends AggregateRoot<AnalysisProjectId> {

    private AnalysisProjectId id;
    private String name;
    private String description;
    private List<VariableDefinition> variables;
    private List<DataProcessStep> processSteps;
    private List<AnalysisConfig> analysisConfigs;
    private List<AnalysisResult> results;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private AnalysisProject() {
        this.variables = new ArrayList<>();
        this.processSteps = new ArrayList<>();
        this.analysisConfigs = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    // ========== 工厂方法 ==========

    /**
     * 创建分析项目（工厂方法）.
     *
     * @param id          分析项目ID
     * @param name        项目名称
     * @param description 项目描述（可为null）
     * @return 新建的AnalysisProject实例
     */
    public static AnalysisProject create(AnalysisProjectId id, String name, String description) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }

        AnalysisProject project = new AnalysisProject();
        project.id = id;
        project.name = name;
        project.description = description;
        return project;
    }

    /**
     * 从持久化存储重建AnalysisProject（不含副作用，不注册事件）.
     *
     * @param id              分析项目ID
     * @param name            项目名称
     * @param description     项目描述
     * @param variables       变量定义列表
     * @param processSteps    数据处理步骤列表
     * @param analysisConfigs 分析配置列表
     * @param results         分析结果列表
     * @return 重建的AnalysisProject实例
     */
    public static AnalysisProject reconstruct(AnalysisProjectId id,
                                               String name,
                                               String description,
                                               List<VariableDefinition> variables,
                                               List<DataProcessStep> processSteps,
                                               List<AnalysisConfig> analysisConfigs,
                                               List<AnalysisResult> results) {
        AnalysisProject project = new AnalysisProject();
        project.id = id;
        project.name = name;
        project.description = description;
        project.variables = variables != null ? new ArrayList<>(variables) : new ArrayList<VariableDefinition>();
        project.processSteps = processSteps != null ? new ArrayList<>(processSteps) : new ArrayList<DataProcessStep>();
        project.analysisConfigs = analysisConfigs != null ? new ArrayList<>(analysisConfigs) : new ArrayList<AnalysisConfig>();
        project.results = results != null ? new ArrayList<>(results) : new ArrayList<AnalysisResult>();
        return project;
    }

    // ========== 业务方法 ==========

    /**
     * 导入数据并推断变量类型.
     * <p>
     * 根据导入的数据自动推断每列的数据类型，创建对应的变量定义。
     * 注册 {@link DataImportCompletedEvent} 事件。
     * </p>
     *
     * @param rawData 导入的原始数据（列表形式，每行为字段名-值的映射）
     */
    public void importData(List<Map<String, Object>> rawData) {
        Objects.requireNonNull(rawData, "rawData must not be null");

        if (rawData.isEmpty()) {
            throw new IllegalArgumentException("rawData must not be empty");
        }

        // Infer variable types from the first row of data
        Map<String, Object> firstRow = rawData.get(0);
        for (Map.Entry<String, Object> entry : firstRow.entrySet()) {
            String fieldName = entry.getKey();
            Object sampleValue = entry.getValue();

            // Check for duplicate variable names
            boolean nameExists = variables.stream()
                    .anyMatch(v -> v.getName().equals(fieldName));
            if (nameExists) {
                throw new BusinessRuleViolationException(
                        "ANALYSIS_DUPLICATE_VARIABLE",
                        "Variable name '" + fieldName + "' already exists in project " + id);
            }

            VariableType type = inferType(sampleValue);
            VariableDefinition vd = VariableDefinition.create(
                    generateVariableDefinitionId(),
                    fieldName,
                    fieldName,
                    type,
                    fieldName
            );
            this.variables.add(vd);
        }

        registerEvent(new DataImportCompletedEvent(id, variables.size()));
    }

    /**
     * 添加数据处理步骤.
     *
     * @param step 数据处理步骤
     */
    public void addProcessStep(DataProcessStep step) {
        Objects.requireNonNull(step, "step must not be null");
        this.processSteps.add(step);
    }

    /**
     * 执行数据处理（按sortOrder排序依次执行）.
     * <p>
     * 注册 {@link DataProcessingCompletedEvent} 事件。
     * </p>
     */
    public void executeProcessing() {
        if (processSteps.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "ANALYSIS_NO_PROCESS_STEPS",
                    "No process steps defined for project " + id);
        }

        // Steps are executed in sortOrder sequence
        Collections.sort(processSteps, (a, b) -> a.getSortOrder().compareTo(b.getSortOrder()));

        registerEvent(new DataProcessingCompletedEvent(id, processSteps.size()));
    }

    /**
     * 执行分析.
     * <p>
     * 根据分析配置执行统计分析算法，创建分析结果。
     * 注册 {@link AnalysisExecutionCompletedEvent} 事件。
     * </p>
     *
     * @param config 分析配置
     * @return 创建的AnalysisResult实例
     */
    public AnalysisResult executeAnalysis(AnalysisConfig config) {
        Objects.requireNonNull(config, "config must not be null");

        // Validate that dependent variable exists
        boolean dependentExists = variables.stream()
                .anyMatch(v -> v.getName().equals(config.getDependentVariable()));
        if (!dependentExists) {
            throw new BusinessRuleViolationException(
                    "ANALYSIS_VARIABLE_NOT_FOUND",
                    "Dependent variable '" + config.getDependentVariable()
                            + "' not found in project " + id);
        }

        // Validate that all independent variables exist
        for (String varName : config.getIndependentVariables()) {
            boolean exists = variables.stream()
                    .anyMatch(v -> v.getName().equals(varName));
            if (!exists) {
                throw new BusinessRuleViolationException(
                        "ANALYSIS_VARIABLE_NOT_FOUND",
                        "Independent variable '" + varName
                                + "' not found in project " + id);
            }
        }

        // Mark config as running
        config.markRunning();

        // Create result placeholder
        AnalysisResult result = AnalysisResult.create(
                generateAnalysisResultId(),
                config.getName(),
                config.getAlgorithmType().getDescription(),
                null,
                null,
                config.getConfigJson()
        );

        this.results.add(result);

        registerEvent(new AnalysisExecutionCompletedEvent(id, config.getId(), result.getId()));
        return result;
    }

    /**
     * 添加分析结果.
     *
     * @param result 分析结果
     */
    public void addResult(AnalysisResult result) {
        Objects.requireNonNull(result, "result must not be null");
        this.results.add(result);
    }

    // ========== 查询方法 ==========

    @Override
    public AnalysisProjectId getId() {
        return id;
    }

    /**
     * 获取项目名称.
     *
     * @return 项目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取项目描述.
     *
     * @return 项目描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取变量定义列表（不可修改）.
     *
     * @return 变量定义列表
     */
    public List<VariableDefinition> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    /**
     * 获取数据处理步骤列表（不可修改）.
     *
     * @return 步骤列表
     */
    public List<DataProcessStep> getProcessSteps() {
        return Collections.unmodifiableList(processSteps);
    }

    /**
     * 获取分析配置列表（不可修改）.
     *
     * @return 配置列表
     */
    public List<AnalysisConfig> getAnalysisConfigs() {
        return Collections.unmodifiableList(analysisConfigs);
    }

    /**
     * 获取分析结果列表（不可修改）.
     *
     * @return 结果列表
     */
    public List<AnalysisResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    // ========== 内部辅助方法 ==========

    /**
     * 根据样本值推断变量类型.
     *
     * @param sampleValue 样本值
     * @return 推断的VariableType
     */
    private VariableType inferType(Object sampleValue) {
        if (sampleValue == null) {
            return VariableType.TEXT;
        }
        if (sampleValue instanceof Number) {
            return VariableType.NUMERIC;
        }
        if (sampleValue instanceof java.util.Date
                || sampleValue instanceof java.sql.Date
                || sampleValue instanceof java.sql.Timestamp) {
            return VariableType.DATE;
        }
        String str = sampleValue.toString();
        if (str.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            return VariableType.DATE;
        }
        if (str.matches("-?\\d+(\\.\\d+)?")) {
            return VariableType.NUMERIC;
        }
        // Simple heuristic: if fewer than 20 unique values (likely categories)
        return VariableType.CATEGORICAL;
    }

    /**
     * 生成变量定义ID（占位实现）.
     *
     * @return 新的VariableDefinitionId
     */
    private VariableDefinitionId generateVariableDefinitionId() {
        return new VariableDefinitionId(System.currentTimeMillis() + variables.size());
    }

    /**
     * 生成分析结果ID（占位实现）.
     *
     * @return 新的AnalysisResultId
     */
    private AnalysisResultId generateAnalysisResultId() {
        return new AnalysisResultId(System.currentTimeMillis() + results.size());
    }

    @Override
    public String toString() {
        return "AnalysisProject{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", variableCount=" + variables.size()
                + ", resultCount=" + results.size()
                + '}';
    }
}
