package com.clinicaltrial.ddd.datacollection.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.datacollection.domain.model.CrfField;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CompletenessCalculationService — 完整性计算领域服务.
 * <p>
 * 负责计算CRF评估的填写完整性。
 * 根据模板字段定义，统计已填写的必填字段与总的必填字段的比例。
 * 隐藏字段和有条件隐藏的字段不计入完整性计算。
 * </p>
 */
@Service
public class CompletenessCalculationService {

    /**
     * 计算CRF评估的完整性.
     * <p>
     * 算法：
     * <ol>
     *   <li>从templateFields中筛选出可计数字段（必填且非隐藏）</li>
     *   <li>统计这些字段中已填写（有非空值）的数量</li>
     *   <li>计算百分比 = filledCount / totalCount * 100</li>
     *   <li>返回Completeness值对象</li>
     * </ol>
     * </p>
     *
     * @param assessment     CRF评估聚合（包含已填写的字段值）
     * @param templateFields CRF模板字段定义列表
     * @return 计算后的完整性值对象
     */
    public Completeness calculate(CrfAssessment assessment, List<CrfField> templateFields) {
        if (templateFields == null || templateFields.isEmpty()) {
            return Completeness.zero();
        }

        // 筛选出应参与计算的字段（必填且非隐藏）
        List<CrfField> countableFields = templateFields.stream()
                .filter(CrfField::isCountable)
                .collect(Collectors.toList());

        int totalCount = countableFields.size();
        if (totalCount == 0) {
            return Completeness.zero();
        }

        // 统计已填写的字段数
        int filledCount = 0;
        List<CrfFieldValue> fieldValues = assessment.getFieldValues();

        for (CrfField field : countableFields) {
            boolean filled = fieldValues.stream()
                    .anyMatch(fv -> fv.matches(field.getFieldCode())
                            && fv.getFieldValue() != null
                            && !fv.getFieldValue().trim().isEmpty());
            if (filled) {
                filledCount++;
            }
        }

        // 计算百分比
        BigDecimal percentage = BigDecimal.valueOf(filledCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount), 2, BigDecimal.ROUND_HALF_UP);

        return new Completeness(percentage, filledCount, totalCount);
    }
}
