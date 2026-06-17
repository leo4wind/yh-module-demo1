package com.clinicaltrial.ddd.datacollection.domain.service;

import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessmentTestFixtures;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CrfStateTransitionService}.
 * <p>
 * Tests the decision logic for CRF assessment state transitions based on
 * completeness evaluation. Pure domain tests — no mocks.
 * </p>
 */
@DisplayName("CrfStateTransitionService 状态转换编排服务测试")
class CrfStateTransitionServiceTest {

    private final CrfStateTransitionService service = new CrfStateTransitionService();

    // ---------------------------------------------------------------
    // Helper: create a 100% completeness
    // ---------------------------------------------------------------

    private Completeness fullCompleteness() {
        return new Completeness(new BigDecimal("100.00"), 2, 2);
    }

    private Completeness partialCompleteness() {
        return new Completeness(new BigDecimal("50.00"), 1, 2);
    }

    private Completeness zeroCompleteness() {
        return Completeness.zero();
    }

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("正案例 — 允许转换")
    class PositiveCases {

        /**
         * 验证PENDING状态的评估在100%完整性时可自动转换到COMPLETED，
         * evaluateAndTransition()返回true
         */
        @Test
        @DisplayName("PENDING 评估 + 100% 完整性 → 返回 true（应转换到 COMPLETED）")
        void pendingAssessmentFullCompletenessReturnsTrue() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            Completeness completeness = fullCompleteness();

            boolean result = service.evaluateAndTransition(assessment, completeness);

            assertThat(result).isTrue();
        }

        /**
         * 验证PENDING状态的评估在部分完整性(50%)时可自动转换到IN_PROGRESS，
         * evaluateAndTransition()返回true
         */
        @Test
        @DisplayName("PENDING 评估 + 部分完整性 → 返回 true（应转换到 IN_PROGRESS）")
        void pendingAssessmentPartialCompletenessReturnsTrue() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            Completeness completeness = partialCompleteness();

            boolean result = service.evaluateAndTransition(assessment, completeness);

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("负案例 — 不允许转换")
    class NegativeCases {

        /**
         * 验证QUERIED状态的评估即使100%完整性也无法自动转换，
         * evaluateAndTransition()返回false，质疑状态禁止自动流转
         */
        @Test
        @DisplayName("QUERIED 评估 + 100% 完整性 → 返回 false")
        void queriedAssessmentReturnsFalse() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aQueriedAssessment();
            Completeness completeness = fullCompleteness();

            boolean result = service.evaluateAndTransition(assessment, completeness);

            assertThat(result).isFalse();
        }

        /**
         * 验证AUDITED状态的评估即使100%完整性也无法自动转换，
         * evaluateAndTransition()返回false，已稽查状态禁止自动流转
         */
        @Test
        @DisplayName("AUDITED 评估 + 100% 完整性 → 返回 false")
        void auditedAssessmentReturnsFalse() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.anAuditedAssessment();
            Completeness completeness = fullCompleteness();

            boolean result = service.evaluateAndTransition(assessment, completeness);

            assertThat(result).isFalse();
        }

        /**
         * 验证COMPLETED状态的评估即使100%完整性也无法自动转换，
         * evaluateAndTransition()返回false，因为已完成状态已是终态不需再转换
         */
        @Test
        @DisplayName("COMPLETED 评估 + 100% 完整性 → 返回 false（已是最简状态）")
        void completedAssessmentFullCompletenessReturnsFalse() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();
            Completeness completeness = fullCompleteness();

            boolean result = service.evaluateAndTransition(assessment, completeness);

            assertThat(result).isFalse();
        }

        /**
         * 验证PENDING状态的评估在0%完整性时不发生转换，
         * evaluateAndTransition()返回false
         */
        @Test
        @DisplayName("PENDING 评估 + 0% 完整性 → 返回 false（无变化）")
        void pendingAssessmentZeroCompletenessReturnsFalse() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            Completeness completeness = zeroCompleteness();

            boolean result = service.evaluateAndTransition(assessment, completeness);

            assertThat(result).isFalse();
        }

        /**
         * 验证IN_PROGRESS状态的评估在0%完整性时不发生转换，
         * evaluateAndTransition()返回false
         */
        @Test
        @DisplayName("IN_PROGRESS 评估 + 0% 完整性 → 返回 false")
        void inProgressAssessmentZeroCompletenessReturnsFalse() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.anInProgressAssessment();
            Completeness completeness = zeroCompleteness();

            boolean result = service.evaluateAndTransition(assessment, completeness);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("守卫条件 — null 参数")
    class GuardConditionTests {

        /**
         * 验证传入null评估时evaluateAndTransition()返回false，
         * 安全处理边界输入不抛出异常
         */
        @Test
        @DisplayName("null 评估 → 返回 false")
        void nullAssessmentReturnsFalse() {
            Completeness completeness = fullCompleteness();

            boolean result = service.evaluateAndTransition(null, completeness);

            assertThat(result).isFalse();
        }

        /**
         * 验证传入null完整性值时evaluateAndTransition()返回false，
         * 安全处理边界输入不抛出异常
         */
        @Test
        @DisplayName("null 完整性 → 返回 false")
        void nullCompletenessReturnsFalse() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            boolean result = service.evaluateAndTransition(assessment, null);

            assertThat(result).isFalse();
        }

        /**
         * 验证评估和完整性值同时为null时evaluateAndTransition()返回false，
         * 安全处理全空边界输入
         */
        @Test
        @DisplayName("两个参数都为 null → 返回 false")
        void bothNullReturnsFalse() {
            boolean result = service.evaluateAndTransition(null, null);

            assertThat(result).isFalse();
        }
    }
}
