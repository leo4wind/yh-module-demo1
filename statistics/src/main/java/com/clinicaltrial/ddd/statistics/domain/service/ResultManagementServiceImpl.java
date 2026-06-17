package com.clinicaltrial.ddd.statistics.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ResultManagementServiceImpl — 分析结果管理领域服务实现.
 */
@Service
public class ResultManagementServiceImpl implements ResultManagementService {

    @Override
    public List<AnalysisResult> getFavorites(List<AnalysisResult> results) {
        Objects.requireNonNull(results, "results must not be null");
        return results.stream()
                .filter(AnalysisResult::isFavorite)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleFavorite(AnalysisResult result) {
        Objects.requireNonNull(result, "result must not be null");
        result.toggleFavorite();
    }

    @Override
    public String generateSummary(AnalysisResult result) {
        Objects.requireNonNull(result, "result must not be null");
        StringBuilder sb = new StringBuilder();
        sb.append("分析方法: ").append(result.getMethod());
        if (result.getResultSummary() != null) {
            sb.append("\n摘要: ").append(result.getResultSummary());
        }
        return sb.toString();
    }
}
