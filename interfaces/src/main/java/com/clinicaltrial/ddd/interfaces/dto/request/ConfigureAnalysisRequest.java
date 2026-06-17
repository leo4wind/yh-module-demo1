package com.clinicaltrial.ddd.interfaces.dto.request;

import java.util.List;

/**
 * 配置分析请求.
 */
public class ConfigureAnalysisRequest {

    private String name;
    private String algorithmType;   // AlgorithmType name
    private String dependentVariable;
    private List<String> independentVariables;
    private String configJson;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAlgorithmType() { return algorithmType; }
    public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }
    public String getDependentVariable() { return dependentVariable; }
    public void setDependentVariable(String dependentVariable) { this.dependentVariable = dependentVariable; }
    public List<String> getIndependentVariables() { return independentVariables; }
    public void setIndependentVariables(List<String> independentVariables) { this.independentVariables = independentVariables; }
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
}
