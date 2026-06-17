package com.clinicaltrial.ddd.datacollection.domain.service;

import com.clinicaltrial.ddd.datacollection.domain.model.CrfField;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessmentTestFixtures;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CompletenessCalculationService}.
 * <p>
 * Pure function tests — verifies the completeness calculation logic
 * with different combinations of template fields and filled values.
 * </p>
 */
@DisplayName("CompletenessCalculationService 完整性计算服务测试")
class CompletenessCalculationServiceTest {

    private final CompletenessCalculationService service = new CompletenessCalculationService();

    // ---------------------------------------------------------------
    // Helper: create assessment with specific field values
    // ---------------------------------------------------------------

    private CrfAssessment assessmentWithValues(CrfFieldValue... values) {
        CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
        for (CrfFieldValue value : values) {
            assessment.saveFieldValue(value, 1L);
        }
        assessment.pullDomainEvents(); // clear field value events
        return assessment;
    }

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("边界条件")
    class BoundaryTests {

        /**
         * 验证传入null模板字段列表时返回Completeness.zero()，
         * 处理边界输入不抛出异常
         */
        @Test
        @DisplayName("null 模板字段返回 Completeness.zero()")
        void nullTemplateFieldsReturnsZero() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            Completeness result = service.calculate(assessment, null);

            assertThat(result).isEqualTo(Completeness.zero());
        }

        /**
         * 验证传入空模板字段列表时返回Completeness.zero()
         */
        @Test
        @DisplayName("空模板字段列表返回 Completeness.zero()")
        void emptyTemplateFieldsReturnsZero() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            Completeness result = service.calculate(assessment, Collections.emptyList());

            assertThat(result).isEqualTo(Completeness.zero());
        }

        /**
         * 验证所有模板字段都为隐藏时返回Completeness.zero()，
         * 即使有字段值也不计入统计
         */
        @Test
        @DisplayName("所有字段隐藏时返回 Completeness.zero()")
        void allHiddenFieldsReturnsZero() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.pullDomainEvents();

            Completeness result = service.calculate(
                    assessment, CrfAssessmentTestFixtures.allHiddenFields());

            assertThat(result).isEqualTo(Completeness.zero());
        }
    }

    @Nested
    @DisplayName("计算逻辑")
    class CalculationTests {

        /**
         * 验证两个必填字段都填写时，完整性计算返回100%，
         * filledCount=2，totalCount=2，isComplete()=true
         */
        @Test
        @DisplayName("所有必填字段填写完整时返回 100%")
        void allRequiredFilledReturns100Percent() {
            CrfAssessment assessment = assessmentWithValues(
                    CrfAssessmentTestFixtures.fieldValue("f1", "v1"),
                    CrfAssessmentTestFixtures.fieldValue("f2", "v2"));

            Completeness result = service.calculate(
                    assessment, CrfAssessmentTestFixtures.twoRequiredFields());

            assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("100.00"));
            assertThat(result.getFilledCount()).isEqualTo(2);
            assertThat(result.getTotalCount()).isEqualTo(2);
            assertThat(result.isComplete()).isTrue();
        }

        /**
         * 验证两个必填字段中只填写一个时，完整性计算返回50%，
         * isComplete()=false
         */
        @Test
        @DisplayName("一半必填字段填写时返回 50%")
        void halfFilledReturns50Percent() {
            CrfAssessment assessment = assessmentWithValues(
                    CrfAssessmentTestFixtures.fieldValue("f1", "v1"));

            Completeness result = service.calculate(
                    assessment, CrfAssessmentTestFixtures.twoRequiredFields());

            assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(result.getFilledCount()).isEqualTo(1);
            assertThat(result.getTotalCount()).isEqualTo(2);
            assertThat(result.isComplete()).isFalse();
        }

        /**
         * 验证隐藏字段(visibility=hidden)不从完整性计算中计入分母，
         * mixedFields中只有f1、f2计入分母，共2个，已填1个=50%
         */
        @Test
        @DisplayName("隐藏字段从完整性计算中排除")
        void hiddenFieldsExcluded() {
            CrfAssessment assessment = assessmentWithValues(
                    CrfAssessmentTestFixtures.fieldValue("f1", "v1"),
                    CrfAssessmentTestFixtures.fieldValue("f4", "v4")); // f4 is hidden

            Completeness result = service.calculate(
                    assessment, CrfAssessmentTestFixtures.mixedFields());

            // Countable: f1, f2 (f3 optional, f4 hidden, f5 condHidden)
            // f1 has value, f2 does not → 1/2 = 50%
            assertThat(result.getTotalCount()).isEqualTo(2);
            assertThat(result.getFilledCount()).isEqualTo(1);
            assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
        }

        /**
         * 验证条件隐藏字段(visibility=condHidden)不从完整性计算中计入分母，
         * mixedFields中只有f1、f2计入分母
         */
        @Test
        @DisplayName("条件隐藏字段从完整性计算中排除")
        void conditionalHiddenFieldsExcluded() {
            CrfAssessment assessment = assessmentWithValues(
                    CrfAssessmentTestFixtures.fieldValue("f1", "v1"),
                    CrfAssessmentTestFixtures.fieldValue("f5", "v5")); // f5 is condHidden

            Completeness result = service.calculate(
                    assessment, CrfAssessmentTestFixtures.mixedFields());

            // Countable: f1, f2 (f5 is condHidden → excluded)
            assertThat(result.getTotalCount()).isEqualTo(2);
            assertThat(result.getFilledCount()).isEqualTo(1);
        }

        /**
         * 验证可选字段(required=false)不计入完整度计算的分母，
         * mixedFields中只有f1、f2计入分母，填写的可选字段f3不算已填写
         */
        @Test
        @DisplayName("可选字段从完整性计算中排除")
        void optionalFieldsExcluded() {
            CrfAssessment assessment = assessmentWithValues(
                    CrfAssessmentTestFixtures.fieldValue("f3", "v3")); // f3 is optional

            Completeness result = service.calculate(
                    assessment, CrfAssessmentTestFixtures.mixedFields());

            // Countable: f1, f2 (f3 is optional → excluded)
            assertThat(result.getTotalCount()).isEqualTo(2);
            assertThat(result.getFilledCount()).isEqualTo(0);
        }

        /**
         * 验证空字符串的字段值视为未填写，filledCount为0
         */
        @Test
        @DisplayName("空字符串值视为未填写")
        void emptyStringValueCountsAsUnfilled() {
            CrfAssessment assessment = assessmentWithValues(
                    CrfAssessmentTestFixtures.emptyFieldValue("f1"));

            Completeness result = service.calculate(
                    assessment, CrfAssessmentTestFixtures.twoRequiredFields());

            assertThat(result.getFilledCount()).isEqualTo(0);
            assertThat(result.getTotalCount()).isEqualTo(2);
        }
    }
}
