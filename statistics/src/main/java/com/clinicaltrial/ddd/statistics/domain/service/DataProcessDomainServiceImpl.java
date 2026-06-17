package com.clinicaltrial.ddd.statistics.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.statistics.domain.model.entity.DataProcessStep;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.DataProcessType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DataProcessDomainServiceImpl — 数据处理领域服务实现.
 */
@Service
public class DataProcessDomainServiceImpl implements DataProcessDomainService {

    private static final Logger log = LoggerFactory.getLogger(DataProcessDomainServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Map<String, Object>> processStep(List<Map<String, Object>> data, DataProcessStep step) {
        Objects.requireNonNull(data, "data must not be null");
        Objects.requireNonNull(step, "step must not be null");

        switch (step.getProcessType()) {
            case FILL_MISSING:
                return processFillMissing(data, step);
            case NORMALIZE:
                return processNormalize(data, step);
            case CATEGORIZE:
                return processCategorize(data, step);
            case MERGE:
                return processMerge(data, step);
            case SPLIT:
                return processSplit(data, step);
            default:
                log.warn("Unsupported process type: {}, returning data as-is", step.getProcessType());
                return data;
        }
    }

    @Override
    public List<Map<String, Object>> processAll(List<Map<String, Object>> data, List<DataProcessStep> steps) {
        Objects.requireNonNull(data, "data must not be null");
        if (steps == null || steps.isEmpty()) {
            return data;
        }

        List<DataProcessStep> sorted = steps.stream()
                .filter(s -> s.getSortOrder() != null)
                .sorted(Comparator.comparingInt(DataProcessStep::getSortOrder))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = data;
        for (DataProcessStep step : sorted) {
            result = processStep(result, step);
        }
        return result;
    }

    private List<Map<String, Object>> processFillMissing(List<Map<String, Object>> data, DataProcessStep step) {
        Map<String, Object> config = parseConfig(step.getConfigJson());
        String targetColumn = (String) config.get("column");
        String method = (String) config.getOrDefault("method", "mean");

        if (targetColumn == null || targetColumn.isEmpty()) {
            return data;
        }

        List<Double> numericValues = data.stream()
                .map(row -> row.get(targetColumn))
                .filter(Objects::nonNull)
                .map(v -> {
                    try { return Double.parseDouble(v.toString()); }
                    catch (NumberFormatException e) { return null; }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Object fillValue;
        switch (method) {
            case "median":
                Collections.sort(numericValues, new Comparator<Double>() {
                    @Override
                    public int compare(Double a, Double b) { return a.compareTo(b); }
                });
                int mid = numericValues.size() / 2;
                fillValue = numericValues.size() % 2 == 0
                        ? (numericValues.get(mid - 1) + numericValues.get(mid)) / 2
                        : numericValues.get(mid);
                break;
            case "mode":
                Map<Double, Long> freqMap = new HashMap<>();
                for (Double v : numericValues) {
                    freqMap.merge(v, 1L, Long::sum);
                }
                fillValue = freqMap.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(0.0);
                break;
            case "value":
                fillValue = config.getOrDefault("fillValue", 0);
                break;
            default: // mean
                fillValue = numericValues.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
                break;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Map<String, Object> newRow = new LinkedHashMap<>(row);
            if (newRow.get(targetColumn) == null || newRow.get(targetColumn).toString().isEmpty()) {
                newRow.put(targetColumn, fillValue);
            }
            result.add(newRow);
        }
        return result;
    }

    private List<Map<String, Object>> processNormalize(List<Map<String, Object>> data, DataProcessStep step) {
        Map<String, Object> config = parseConfig(step.getConfigJson());
        String targetColumn = (String) config.get("column");
        String method = (String) config.getOrDefault("method", "minmax");

        if (targetColumn == null || targetColumn.isEmpty()) {
            return data;
        }

        List<Double> values = data.stream()
                .map(row -> row.get(targetColumn))
                .filter(Objects::nonNull)
                .map(v -> {
                    try { return Double.parseDouble(v.toString()); }
                    catch (NumberFormatException e) { return null; }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (values.isEmpty()) return data;

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        double sumSq = 0;
        for (Double v : values) {
            if (v < min) min = v;
            if (v > max) max = v;
            sum += v;
            sumSq += v * v;
        }
        double mean = sum / values.size();
        double variance = sumSq / values.size() - mean * mean;
        double std = Math.sqrt(Math.max(variance, 0));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Map<String, Object> newRow = new LinkedHashMap<>(row);
            Object val = row.get(targetColumn);
            if (val != null) {
                try {
                    double d = Double.parseDouble(val.toString());
                    if ("zscore".equals(method) && std > 0) {
                        newRow.put(targetColumn, (d - mean) / std);
                    } else if (max > min) {
                        newRow.put(targetColumn, (d - min) / (max - min));
                    }
                } catch (NumberFormatException e) {
                    // keep original
                }
            }
            result.add(newRow);
        }
        return result;
    }

    private List<Map<String, Object>> processCategorize(List<Map<String, Object>> data, DataProcessStep step) {
        return data;
    }

    private List<Map<String, Object>> processMerge(List<Map<String, Object>> data, DataProcessStep step) {
        return data;
    }

    private List<Map<String, Object>> processSplit(List<Map<String, Object>> data, DataProcessStep step) {
        return data;
    }

    private Map<String, Object> parseConfig(String configJson) {
        if (configJson == null || configJson.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse config JSON: {}", configJson, e);
            return Collections.emptyMap();
        }
    }
}
