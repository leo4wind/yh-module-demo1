package com.clinicaltrial.ddd.statistics.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * VariableInferenceServiceImpl — 变量类型推断领域服务实现.
 */
@Service
public class VariableInferenceServiceImpl implements VariableInferenceService {

    private static final double CATEGORICAL_THRESHOLD = 0.05;
    private static final int MIN_ROWS_FOR_CATEGORICAL = 20;
    private static final String[] DATE_PATTERNS = {
            "yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy", "dd/MM/yyyy", "yyyyMMdd"
    };

    @Override
    public Map<String, VariableType> inferTypes(List<Map<String, Object>> data, int sampleSize) {
        Map<String, VariableType> result = new LinkedHashMap<>();
        if (data == null || data.isEmpty()) {
            return result;
        }

        List<Map<String, Object>> sample = data;
        if (sampleSize > 0 && sampleSize < data.size()) {
            sample = data.subList(0, sampleSize);
        }

        Set<String> columns = data.get(0).keySet();
        int totalRows = sample.size();

        for (String col : columns) {
            List<String> values = new ArrayList<>();
            boolean allNumeric = true;
            for (Map<String, Object> row : sample) {
                Object val = row.get(col);
                String str = val != null ? val.toString() : "";
                values.add(str);
                if (!str.isEmpty() && allNumeric) {
                    try {
                        Double.parseDouble(str);
                    } catch (NumberFormatException e) {
                        allNumeric = false;
                    }
                }
            }

            long nonEmpty = values.stream().filter(v -> !v.isEmpty()).count();
            if (nonEmpty == 0) {
                result.put(col, VariableType.TEXT);
                continue;
            }

            if (allNumeric) {
                result.put(col, VariableType.NUMERIC);
                continue;
            }

            if (isDateColumn(values)) {
                result.put(col, VariableType.DATE);
                continue;
            }

            Set<String> uniqueValues = new HashSet<>(values);
            double uniqueRatio = (double) uniqueValues.size() / totalRows;
            if (totalRows > MIN_ROWS_FOR_CATEGORICAL && uniqueRatio < CATEGORICAL_THRESHOLD) {
                result.put(col, VariableType.CATEGORICAL);
                continue;
            }

            result.put(col, VariableType.TEXT);
        }
        return result;
    }

    @Override
    public VariableType inferTypeByFieldName(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return null;
        }
        String lower = fieldName.toLowerCase();
        if (Pattern.matches(".*\\b(age|身高|体重|bmi|score|count|num|数量|值)\\b.*", lower)) {
            return VariableType.NUMERIC;
        }
        if (Pattern.matches(".*\\b(date|日期|时间|visit|访视)\\b.*", lower)) {
            return VariableType.DATE;
        }
        if (Pattern.matches(".*\\b(sex|gender|种族|race|组别|group|category|类别|等级|grade|stage|分期)\\b.*", lower)) {
            return VariableType.CATEGORICAL;
        }
        if (Pattern.matches(".*\\b(grade|rank|stage|level|分级|分期|评分)\\b.*", lower)) {
            return VariableType.ORDINAL;
        }
        return null;
    }

    private boolean isDateColumn(List<String> values) {
        for (String pattern : DATE_PATTERNS) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            int parsed = 0;
            int checked = 0;
            for (String v : values) {
                if (v.isEmpty()) continue;
                checked++;
                try {
                    sdf.parse(v);
                    parsed++;
                } catch (ParseException e) {
                    break;
                }
            }
            if (checked > 0 && parsed == checked) {
                return true;
            }
        }
        return false;
    }
}
