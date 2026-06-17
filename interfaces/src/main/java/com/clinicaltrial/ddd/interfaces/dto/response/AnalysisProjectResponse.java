package com.clinicaltrial.ddd.interfaces.dto.response;

import java.util.Date;
import java.util.List;

/**
 * 分析项目响应.
 */
public class AnalysisProjectResponse {

    private Long id;
    private String name;
    private String description;
    private List<VariableVo> variables;
    private List<AnalysisConfigVo> analysisConfigs;
    private List<AnalysisResultVo> results;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<VariableVo> getVariables() { return variables; }
    public void setVariables(List<VariableVo> variables) { this.variables = variables; }
    public List<AnalysisConfigVo> getAnalysisConfigs() { return analysisConfigs; }
    public void setAnalysisConfigs(List<AnalysisConfigVo> analysisConfigs) { this.analysisConfigs = analysisConfigs; }
    public List<AnalysisResultVo> getResults() { return results; }
    public void setResults(List<AnalysisResultVo> results) { this.results = results; }

    public static class VariableVo {
        private Long id;
        private String name;
        private String label;
        private String variableType;
        private String sourceField;
        private Boolean derived;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getVariableType() { return variableType; }
        public void setVariableType(String variableType) { this.variableType = variableType; }
        public String getSourceField() { return sourceField; }
        public void setSourceField(String sourceField) { this.sourceField = sourceField; }
        public Boolean getDerived() { return derived; }
        public void setDerived(Boolean derived) { this.derived = derived; }
    }

    public static class AnalysisConfigVo {
        private Long id;
        private String name;
        private String algorithmType;
        private String dependentVariable;
        private List<String> independentVariables;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAlgorithmType() { return algorithmType; }
        public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }
        public String getDependentVariable() { return dependentVariable; }
        public void setDependentVariable(String dependentVariable) { this.dependentVariable = dependentVariable; }
        public List<String> getIndependentVariables() { return independentVariables; }
        public void setIndependentVariables(List<String> independentVariables) { this.independentVariables = independentVariables; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class AnalysisResultVo {
        private Long id;
        private String name;
        private String method;
        private String data;
        private String resultSummary;
        private Boolean isFavorite;
        private Date createTime;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        public String getResultSummary() { return resultSummary; }
        public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }
        public Boolean getIsFavorite() { return isFavorite; }
        public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
    }
}
