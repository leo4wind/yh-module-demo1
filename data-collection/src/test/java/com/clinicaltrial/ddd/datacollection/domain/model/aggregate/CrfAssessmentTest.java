package com.clinicaltrial.ddd.datacollection.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfAuditedEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfCompletedEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfCompletenessChangedEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfFieldValueChangedEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.AssessmentScore;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.MonitoringStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive unit tests for {@link CrfAssessment}.
 * <p>
 * Covers all lifecycle state transitions, factory methods, field value management,
 * completeness calculation, query management, auditing, and auto-scoring.
 * Pure domain tests — no mocks.
 * </p>
 */
@DisplayName("CrfAssessment 状态机测试")
class CrfAssessmentTest {

    // ===============================================================
    // Factory Tests
    // ===============================================================

    @Nested
    @DisplayName("工厂方法")
    class FactoryTests {

        /**
         * 验证通过create()创建的CRF评估初始状态为PENDING(0)，完整度为0%，
         * 无字段值、无评分、不产生领域事件
         */
        @Test
        @DisplayName("create 创建 PENDING 状态的评估，完整性为0%，无领域事件")
        void createCreatesPendingAssessment() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            assertThat(assessment.getId()).isEqualTo(CrfAssessmentTestFixtures.aId());
            assertThat(assessment.getSubjectsUserId()).isEqualTo(CrfAssessmentTestFixtures.aSubjectId());
            assertThat(assessment.getCrfId()).isEqualTo(CrfAssessmentTestFixtures.aCrfId());
            assertThat(assessment.getCrfVersionId()).isEqualTo(CrfAssessmentTestFixtures.aVersionId());
            assertThat(assessment.getSubjectsStageId()).isEqualTo(CrfAssessmentTestFixtures.aStageId());
            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.PENDING);
            assertThat(assessment.getCompleteness()).isEqualTo(Completeness.zero());
            assertThat(assessment.isAdverseEvent()).isFalse();
            assertThat(assessment.getAssessmentScore()).isNull();
            assertThat(assessment.getFieldValues()).isEmpty();
            assertThat(assessment.pullDomainEvents()).isEmpty();
        }

        /**
         * 验证reconstruct()正确恢复CRF评估的所有字段值，包含ID、状态、完整性、
         * 字段值列表等，且不产生任何领域事件
         */
        @Test
        @DisplayName("reconstruct 恢复所有字段，无领域事件")
        void reconstructRestoresState() {
            CrfAssessment original = CrfAssessmentTestFixtures.aCompletedAssessment();

            CrfAssessment reconstructed = CrfAssessment.reconstruct(
                    original.getId(),
                    original.getSubjectsUserId(),
                    original.getCrfId(),
                    original.getCrfVersionId(),
                    original.getSubjectsStageId(),
                    original.getStatus(),
                    original.getCompleteness(),
                    original.isAdverseEvent(),
                    original.getAssessmentScore(),
                    original.getFieldValues()
            );

            assertThat(reconstructed.getId()).isEqualTo(original.getId());
            assertThat(reconstructed.getStatus()).isEqualTo(MonitoringStatus.COMPLETED);
            assertThat(reconstructed.getCompleteness()).isEqualTo(original.getCompleteness());
            assertThat(reconstructed.getFieldValues()).hasSize(2);
            assertThat(reconstructed.pullDomainEvents()).isEmpty();
        }
    }

    // ===============================================================
    // Save Field Value Tests
    // ===============================================================

    @Nested
    @DisplayName("保存字段值")
    class SaveFieldValueTests {

        /**
         * 验证PENDING状态下新增字段值：字段被添加到列表、触发CrfFieldValueChangedEvent、
         * 事件中包含正确的评估ID、字段编码、旧值为null、新值为"value1"
         */
        @Test
        @DisplayName("PENDING 状态下新增字段值，添加到列表并触发 CrfFieldValueChangedEvent")
        void addsFieldValueWhenPending() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "value1"), 1L);

            assertThat(assessment.getFieldValues()).hasSize(1);
            assertThat(assessment.getFieldValues().get(0).getFieldCode()).isEqualTo("f1");
            assertThat(assessment.getFieldValues().get(0).getFieldValue()).isEqualTo("value1");

            List<DomainEvent> events = assessment.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(CrfFieldValueChangedEvent.class);

            CrfFieldValueChangedEvent event = (CrfFieldValueChangedEvent) events.get(0);
            assertThat(event.getAssessmentId()).isEqualTo(CrfAssessmentTestFixtures.aId());
            assertThat(event.getFieldCode()).isEqualTo("f1");
            assertThat(event.getOldValue()).isNull(); // new field, no old value
            assertThat(event.getNewValue()).isEqualTo("value1");
            assertThat(event.getUserId()).isEqualTo(1L);
        }

        /**
         * 验证IN_PROGRESS状态下新增字段值：在已有的f1基础上添加f3，
         * 字段列表变为2个，触发CrfFieldValueChangedEvent
         */
        @Test
        @DisplayName("IN_PROGRESS 状态下新增字段值")
        void addsFieldValueWhenInProgress() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.anInProgressAssessment();
            assessment.pullDomainEvents(); // clear previous events

            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f3", "value3"), 2L);

            assertThat(assessment.getFieldValues()).hasSize(2); // f1 (from in-progress setup) + f3
            assertThat(assessment.pullDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(CrfFieldValueChangedEvent.class);
        }

        /**
         * 验证更新已有字段值时：字段值被覆盖、事件中包含旧值"oldValue"和新值"newValue"、
         * 操作用户ID为2L
         */
        @Test
        @DisplayName("更新已有字段值（同编码），事件中包含旧值")
        void updatesExistingFieldValue() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "oldValue"), 1L);
            assessment.pullDomainEvents(); // clear first event

            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "newValue"), 2L);

            assertThat(assessment.getFieldValues()).hasSize(1);
            assertThat(assessment.getFieldValues().get(0).getFieldValue()).isEqualTo("newValue");

            List<DomainEvent> events = assessment.pullDomainEvents();
            assertThat(events).hasSize(1);
            CrfFieldValueChangedEvent event = (CrfFieldValueChangedEvent) events.get(0);
            assertThat(event.getOldValue()).isEqualTo("oldValue");
            assertThat(event.getNewValue()).isEqualTo("newValue");
            assertThat(event.getUserId()).isEqualTo(2L);
        }

        /**
         * 验证COMPLETED状态下保存字段值被拒绝，
         * 抛出BusinessRuleViolationException，提示信息包含"Cannot save field value"
         */
        @Test
        @DisplayName("COMPLETED 状态下保存字段值抛出 BusinessRuleViolationException")
        void saveFieldValueWhenCompletedThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();

            assertThatThrownBy(() ->
                    assessment.saveFieldValue(
                            CrfAssessmentTestFixtures.fieldValue("f1", "x"), 1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot save field value");
        }

        /**
         * 验证QUERIED状态下保存字段值被拒绝，
         * 抛出BusinessRuleViolationException，提示信息包含"Cannot save field value"
         */
        @Test
        @DisplayName("QUERIED 状态下保存字段值抛出 BusinessRuleViolationException")
        void saveFieldValueWhenQueriedThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aQueriedAssessment();
            assessment.pullDomainEvents();

            assertThatThrownBy(() ->
                    assessment.saveFieldValue(
                            CrfAssessmentTestFixtures.fieldValue("f1", "x"), 1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot save field value");
        }

        /**
         * 验证AUDITED状态下保存字段值被拒绝，
         * 抛出BusinessRuleViolationException，提示信息包含"Cannot save field value"
         */
        @Test
        @DisplayName("AUDITED 状态下保存字段值抛出 BusinessRuleViolationException")
        void saveFieldValueWhenAuditedThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.anAuditedAssessment();
            assessment.pullDomainEvents();

            assertThatThrownBy(() ->
                    assessment.saveFieldValue(
                            CrfAssessmentTestFixtures.fieldValue("f1", "x"), 1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot save field value");
        }
    }

    // ===============================================================
    // Completeness Tests
    // ===============================================================

    @Nested
    @DisplayName("完整度计算")
    class CompletenessTests {

        /**
         * 验证无字段填写时完整性为0%，状态保持PENDING，
         * filledCount为0，totalCount为2
         */
        @Test
        @DisplayName("无字段填写时完整性为0%，状态保持 PENDING")
        void zeroPercentWhenNoFieldsFilled() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields());

            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.PENDING);
            assertThat(assessment.getCompleteness().getPercentage())
                    .isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(assessment.getCompleteness().getFilledCount()).isEqualTo(0);
            assertThat(assessment.getCompleteness().getTotalCount()).isEqualTo(2);
        }

        /**
         * 验证当2个必填字段中填写了1个时，完整度=50%，
         * 状态从PENDING自动流转为IN_PROGRESS(1)，触发CrfCompletenessChangedEvent
         */
        @Test
        @DisplayName("50% 填写时 PENDING→IN_PROGRESS，触发 CrfCompletenessChangedEvent")
        void fiftyPercentTransitionsToInProgress() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.pullDomainEvents(); // clear field value event

            assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields());

            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.IN_PROGRESS);
            assertThat(assessment.getCompleteness().getPercentage())
                    .isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(assessment.getCompleteness().getFilledCount()).isEqualTo(1);
            assertThat(assessment.getCompleteness().getTotalCount()).isEqualTo(2);

            List<DomainEvent> events = assessment.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(CrfCompletenessChangedEvent.class);

            CrfCompletenessChangedEvent event = (CrfCompletenessChangedEvent) events.get(0);
            assertThat(event.getOldCompleteness()).isEqualTo(Completeness.zero());
            assertThat(event.getNewCompleteness().getPercentage())
                    .isEqualByComparingTo(new BigDecimal("50.00"));
        }

        /**
         * 验证当2个必填字段全部填写时，完整度=100%，
         * 状态从IN_PROGRESS自动流转为COMPLETED(2)，依次触发
         * CrfCompletenessChangedEvent和CrfCompletedEvent
         */
        @Test
        @DisplayName("100% 填写时 IN_PROGRESS→COMPLETED，触发 CrfCompletedEvent")
        void hundredPercentTransitionsToCompleted() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f2", "v2"), 1L);
            assessment.pullDomainEvents(); // clear field value events

            assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields());

            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.COMPLETED);
            assertThat(assessment.getCompleteness().getPercentage())
                    .isEqualByComparingTo(new BigDecimal("100.00"));
            assertThat(assessment.getCompleteness().getFilledCount()).isEqualTo(2);
            assertThat(assessment.getCompleteness().getTotalCount()).isEqualTo(2);

            List<DomainEvent> events = assessment.pullDomainEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(CrfCompletenessChangedEvent.class);
            assertThat(events.get(1)).isInstanceOf(CrfCompletedEvent.class);

            CrfCompletedEvent completedEvent = (CrfCompletedEvent) events.get(1);
            assertThat(completedEvent.getAssessmentId()).isEqualTo(CrfAssessmentTestFixtures.aId());
            assertThat(completedEvent.getSubjectStageId()).isEqualTo(CrfAssessmentTestFixtures.aStageId());
        }

        /**
         * 验证隐藏字段(visibility=hidden)不计入完整度计算的分母，
         * mixedFields中只有f1、f2两个必填字段计入分母
         */
        @Test
        @DisplayName("隐藏字段不计入分母")
        void hiddenFieldsExcludedFromDenominator() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.pullDomainEvents();

            // f1: required, f2: required, f4: hidden (excluded)
            assessment.calculateCompleteness(CrfAssessmentTestFixtures.mixedFields());

            // f1 is filled, but f4 (hidden) is excluded → total=2, filled=1 from f1
            // However f2 is required but not filled... wait f2 is requiredField("f2") which is required, not hidden.
            // Let's re-check: mixedFields = [f1(required), f2(required), f3(optional), f4(hidden), f5(condHidden)]
            // Countable: f1, f2 (required, not hidden, not condHidden)
            // f1 is filled → 1/2 = 50%
            assertThat(assessment.getCompleteness().getTotalCount()).isEqualTo(2);
            assertThat(assessment.getCompleteness().getFilledCount()).isEqualTo(1);
            assertThat(assessment.getCompleteness().getPercentage())
                    .isEqualByComparingTo(new BigDecimal("50.00"));
        }

        /**
         * 验证条件隐藏字段(visibility=condHidden)不计入完整度计算的分母，
         * mixedFields中只有f1、f2两个必填字段计入分母
         */
        @Test
        @DisplayName("条件隐藏字段不计入分母")
        void conditionalHiddenFieldsExcludedFromDenominator() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.pullDomainEvents();

            // Countable from mixedFields: f1, f2 (f3 optional, f4 hidden, f5 condHidden → excluded)
            assessment.calculateCompleteness(CrfAssessmentTestFixtures.mixedFields());

            assertThat(assessment.getCompleteness().getTotalCount()).isEqualTo(2);
            assertThat(assessment.getCompleteness().getFilledCount()).isEqualTo(1);
        }

        /**
         * 验证可选字段(required=false)不计入完整度计算的分母，
         * mixedFields中只有f1、f2两个必填字段计入分母，即使填写了可选字段f3也不算已填写
         */
        @Test
        @DisplayName("可选字段不计入分母")
        void optionalFieldsExcludedFromDenominator() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f3", "v3"), 1L);
            assessment.pullDomainEvents();

            // Countable from mixedFields: f1, f2 (f3 is optional → excluded)
            assessment.calculateCompleteness(CrfAssessmentTestFixtures.mixedFields());

            assertThat(assessment.getCompleteness().getTotalCount()).isEqualTo(2);
            // f3 value exists but it's optional → not counted as filled for required fields
            assertThat(assessment.getCompleteness().getFilledCount()).isEqualTo(0);
        }

        /**
         * 验证空字符串的字段值视为未填写，filledCount为0，
         * 完整性百分比为0%
         */
        @Test
        @DisplayName("空字符串字段值视为未填写")
        void emptyStringValueCountsAsUnfilled() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.emptyFieldValue("f1"), 1L);
            assessment.pullDomainEvents();

            assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields());

            assertThat(assessment.getCompleteness().getFilledCount()).isEqualTo(0);
            assertThat(assessment.getCompleteness().getTotalCount()).isEqualTo(2);
            assertThat(assessment.getCompleteness().getPercentage())
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }

        /**
         * 验证当完整度与上次计算相同时不触发重复的领域事件，
         * 确保事件仅在实际变化时发布
         */
        @Test
        @DisplayName("完整性未变化时不触发重复事件")
        void completenessUnchangedRaisesNoEvents() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.pullDomainEvents();
            // First calculation: 50% → IN_PROGRESS
            assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields());
            assessment.pullDomainEvents(); // clear events from first calculation

            // Second calculation with same data: completeness unchanged
            assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields());

            assertThat(assessment.pullDomainEvents()).isEmpty();
        }

        /**
         * 验证所有字段都隐藏时totalCount=0、percentage=0，
         * 状态保持PENDING不变
         */
        @Test
        @DisplayName("所有字段隐藏时 totalCount=0，percentage=0，状态保持 PENDING")
        void allHiddenFieldsReturnsZero() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.pullDomainEvents();

            assessment.calculateCompleteness(CrfAssessmentTestFixtures.allHiddenFields());

            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.PENDING);
            assertThat(assessment.getCompleteness()).isEqualTo(Completeness.zero());
        }
    }

    // ===============================================================
    // State Transition Tests
    // ===============================================================

    @Nested
    @DisplayName("状态转换")
    class StateTransitionTests {

        /**
         * 验证完整流程：PENDING(0)→通过填50%转IN_PROGRESS(1)→再填满100%转COMPLETED(2)，
         * 最终发布CrfCompletenessChangedEvent和CrfCompletedEvent两个事件
         */
        @Test
        @DisplayName("完整流程：PENDING → IN_PROGRESS → COMPLETED")
        void fullFlowPendingToInProgressToCompleted() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            // First field: PENDING → IN_PROGRESS (50%)
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.pullDomainEvents();
            assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields());
            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.IN_PROGRESS);
            assessment.pullDomainEvents();

            // Second field: IN_PROGRESS → COMPLETED (100%)
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f2", "v2"), 1L);
            assessment.pullDomainEvents();
            assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields());
            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.COMPLETED);

            List<DomainEvent> events = assessment.pullDomainEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(CrfCompletenessChangedEvent.class);
            assertThat(events.get(1)).isInstanceOf(CrfCompletedEvent.class);
        }

        /**
         * 验证QUERIED状态下调用calculateCompleteness()抛出BusinessRuleViolationException，
         * 处于质疑状态的评估禁止自动状态转换
         */
        @Test
        @DisplayName("QUERIED 状态下计算完整性抛出 BusinessRuleViolationException")
        void cannotAutoTransitionWhenQueried() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aQueriedAssessment();

            assertThatThrownBy(() ->
                    assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot auto-transition");
        }

        /**
         * 验证AUDITED状态下调用calculateCompleteness()抛出BusinessRuleViolationException，
         * 已稽查的评估禁止自动状态转换
         */
        @Test
        @DisplayName("AUDITED 状态下计算完整性抛出 BusinessRuleViolationException")
        void cannotAutoTransitionWhenAudited() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.anAuditedAssessment();

            assertThatThrownBy(() ->
                    assessment.calculateCompleteness(CrfAssessmentTestFixtures.twoRequiredFields()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot auto-transition");
        }
    }

    // ===============================================================
    // Query Management Integration Tests
    // ===============================================================

    @Nested
    @DisplayName("质疑联动")
    class QueryIntegrationTests {

        /**
         * 验证CRF评估从已完成(COMPLETED)被质疑后变为被质疑(QUERIED)，
         * 状态码从2变为3
         */
        @Test
        @DisplayName("raiseQuery: COMPLETED→QUERIED(2→3)")
        void raiseQueryFromCompleted() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();

            assessment.raiseQuery();

            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.QUERIED);
        }

        /**
         * 验证PENDING状态下发起质疑被拒绝，
         * 抛出BusinessRuleViolationException，提示信息包含"must be COMPLETED"
         */
        @Test
        @DisplayName("PENDING 状态下 raiseQuery 抛出 BusinessRuleViolationException")
        void raiseQueryWhenPendingThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            assertThatThrownBy(assessment::raiseQuery)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("must be COMPLETED");
        }

        /**
         * 验证IN_PROGRESS状态下发起质疑被拒绝，
         * 抛出BusinessRuleViolationException，提示信息包含"must be COMPLETED"
         */
        @Test
        @DisplayName("IN_PROGRESS 状态下 raiseQuery 抛出 BusinessRuleViolationException")
        void raiseQueryWhenInProgressThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.anInProgressAssessment();

            assertThatThrownBy(assessment::raiseQuery)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("must be COMPLETED");
        }

        /**
         * 验证已处于QUERIED状态的评估无法再次发起质疑，
         * 抛出BusinessRuleViolationException
         */
        @Test
        @DisplayName("已 QUERIED 状态下再次 raiseQuery 抛出 BusinessRuleViolationException")
        void raiseQueryWhenAlreadyQueriedThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aQueriedAssessment();

            assertThatThrownBy(assessment::raiseQuery)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("must be COMPLETED");
        }

        /**
         * 验证解决所有质疑后，状态从QUERIED(3)转回COMPLETED(2)
         */
        @Test
        @DisplayName("resolveAllQueries: QUERIED→COMPLETED(3→2)")
        void resolveAllQueriesFromQueried() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aQueriedAssessment();

            assessment.resolveAllQueries();

            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.COMPLETED);
        }

        /**
         * 验证COMPLETED状态（非质疑状态）调用resolveAllQueries()被拒绝，
         * 抛出BusinessRuleViolationException，提示信息包含"must be QUERIED"
         */
        @Test
        @DisplayName("COMPLETED 状态下 resolveAllQueries 抛出 BusinessRuleViolationException")
        void resolveAllQueriesWhenCompletedThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();

            assertThatThrownBy(assessment::resolveAllQueries)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("must be QUERIED");
        }
    }

    // ===============================================================
    // Audit Tests
    // ===============================================================

    @Nested
    @DisplayName("稽查")
    class AuditTests {

        /**
         * 验证稽查操作将COMPLETED(2)状态的评估流转为AUDITED(4)，
         * 触发CrfAuditedEvent，事件中包含稽查用户ID
         */
        @Test
        @DisplayName("COMPLETED→AUDITED(2→4)，触发 CrfAuditedEvent")
        void auditFromCompleted() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();

            assessment.audit(100L);

            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.AUDITED);

            List<DomainEvent> events = assessment.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(CrfAuditedEvent.class);

            CrfAuditedEvent event = (CrfAuditedEvent) events.get(0);
            assertThat(event.getAssessmentId()).isEqualTo(CrfAssessmentTestFixtures.aId());
            assertThat(event.getAuditUserId()).isEqualTo(100L);
        }

        /**
         * 验证稽查操作将QUERIED(3)状态的评估流转为AUDITED(4)，
         * 触发CrfAuditedEvent，稽查用户ID为200L
         */
        @Test
        @DisplayName("QUERIED→AUDITED(3→4)，触发 CrfAuditedEvent")
        void auditFromQueried() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aQueriedAssessment();
            assessment.pullDomainEvents();

            assessment.audit(200L);

            assertThat(assessment.getStatus()).isEqualTo(MonitoringStatus.AUDITED);

            List<DomainEvent> events = assessment.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(CrfAuditedEvent.class);

            CrfAuditedEvent event = (CrfAuditedEvent) events.get(0);
            assertThat(event.getAuditUserId()).isEqualTo(200L);
        }

        /**
         * 验证PENDING状态的评估无法进行稽查操作，
         * 抛出BusinessRuleViolationException，提示信息包含"Cannot audit"
         */
        @Test
        @DisplayName("PENDING 状态下 audit 抛出 BusinessRuleViolationException")
        void auditWhenPendingThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            assertThatThrownBy(() -> assessment.audit(1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot audit");
        }

        /**
         * 验证IN_PROGRESS状态的评估无法进行稽查操作，
         * 抛出BusinessRuleViolationException
         */
        @Test
        @DisplayName("IN_PROGRESS 状态下 audit 抛出 BusinessRuleViolationException")
        void auditWhenInProgressThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.anInProgressAssessment();

            assertThatThrownBy(() -> assessment.audit(1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot audit");
        }
    }

    // ===============================================================
    // Auto-Scoring Tests
    // ===============================================================

    @Nested
    @DisplayName("自动评分")
    class AutoScoringTests {

        /**
         * 验证COMPLETED状态下调用autoScore()基于评分规则计算得分：
         * f1=1分 + f2=2分 → total=3分 → result="normal"
         */
        @Test
        @DisplayName("COMPLETED 状态下 autoScore 设置 AssessmentScore，result 基于规则")
        void autoScoreWhenCompleted() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();

            assessment.autoScore(CrfAssessmentTestFixtures.scoringRules());

            AssessmentScore score = assessment.getAssessmentScore();
            assertThat(score).isNotNull();
            // f1=1, f2=2 → total=3 → result="normal"
            assertThat(score.getScore()).isEqualTo("3");
            assertThat(score.getResult()).isEqualTo("normal");
        }

        /**
         * 验证PENDING状态的评估无法进行自动评分，
         * 抛出BusinessRuleViolationException，提示信息包含"Cannot auto-score"
         */
        @Test
        @DisplayName("PENDING 状态下 autoScore 抛出 BusinessRuleViolationException")
        void autoScoreWhenPendingThrows() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();

            assertThatThrownBy(() ->
                    assessment.autoScore(CrfAssessmentTestFixtures.scoringRules()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot auto-score");
        }

        /**
         * 验证传入null评分规则时自动评分的result为"no-rules"，
         * score为"0"
         */
        @Test
        @DisplayName("null 评分规则返回 result=no-rules")
        void autoScoreWithNullRules() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();

            assessment.autoScore(null);

            AssessmentScore score = assessment.getAssessmentScore();
            assertThat(score).isNotNull();
            assertThat(score.getScore()).isEqualTo("0");
            assertThat(score.getResult()).isEqualTo("no-rules");
        }

        /**
         * 验证传入空Map评分规则时自动评分的result为"no-rules"，
         * score为"0"
         */
        @Test
        @DisplayName("空评分规则返回 result=no-rules")
        void autoScoreWithEmptyRules() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();

            assessment.autoScore(new HashMap<>());

            AssessmentScore score = assessment.getAssessmentScore();
            assertThat(score.getScore()).isEqualTo("0");
            assertThat(score.getResult()).isEqualTo("no-rules");
        }

        /**
         * 验证字段值不匹配任何评分规则时自动评分的result为"no-matching-rules"，
         * score为"0"
         */
        @Test
        @DisplayName("字段值不匹配任何规则时返回 result=no-matching-rules")
        void autoScoreWithNoMatchingFieldValues() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aCompletedAssessment();
            assessment.pullDomainEvents();

            Map<String, BigDecimal> rules = new HashMap<>();
            rules.put("nonexistent", BigDecimal.TEN);

            assessment.autoScore(rules);

            AssessmentScore score = assessment.getAssessmentScore();
            assertThat(score.getScore()).isEqualTo("0");
            assertThat(score.getResult()).isEqualTo("no-matching-rules");
        }
    }

    // ===============================================================
    // Guard Condition Tests
    // ===============================================================

    @Nested
    @DisplayName("守卫条件")
    class GuardConditionTests {

        /**
         * 验证isCompleted()对QUERIED(3)和AUDITED(4)状态返回true，
         * 对PENDING(0)返回false，表示状态码>=COMPLETED(2)均视为已完成
         */
        @Test
        @DisplayName("isCompleted 返回 true 当状态 >= COMPLETED")
        void isCompletedTrueForCompletedAndAbove() {
            CrfAssessment pending = CrfAssessmentTestFixtures.aPendingAssessment();
            assertThat(pending.isCompleted()).isFalse();

            CrfAssessment queried = CrfAssessmentTestFixtures.aQueriedAssessment();
            assertThat(queried.isCompleted()).isTrue();

            CrfAssessment audited = CrfAssessmentTestFixtures.anAuditedAssessment();
            assertThat(audited.isCompleted()).isTrue();
        }

        /**
         * 验证isAudited()仅对AUDITED(4)状态返回true，
         * 对PENDING和COMPLETED返回false
         */
        @Test
        @DisplayName("isAudited 返回 true 当状态为 AUDITED")
        void isAuditedTrueForAudited() {
            CrfAssessment audited = CrfAssessmentTestFixtures.anAuditedAssessment();
            assertThat(audited.isAudited()).isTrue();

            CrfAssessment pending = CrfAssessmentTestFixtures.aPendingAssessment();
            assertThat(pending.isAudited()).isFalse();

            CrfAssessment completed = CrfAssessmentTestFixtures.aCompletedAssessment();
            assertThat(completed.isAudited()).isFalse();
        }

        /**
         * 验证findFieldValueByCode()通过字段编码正确查找已保存的字段值，
         * 不存在的编码返回Optional.empty()
         */
        @Test
        @DisplayName("findFieldValueByCode 返回匹配的字段值")
        void findFieldValueByCodeReturnsMatchingValue() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);

            assertThat(assessment.findFieldValueByCode("f1"))
                    .isPresent()
                    .hasValueSatisfying(fv -> assertThat(fv.getFieldValue()).isEqualTo("v1"));
            assertThat(assessment.findFieldValueByCode("nonexistent")).isNotPresent();
        }

        /**
         * 验证isFieldFilled()对有值的字段返回true，
         * 空字符串和无此字段均返回false
         */
        @Test
        @DisplayName("isFieldFilled 正确判断字段是否填写")
        void isFieldFilledReturnsCorrectly() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);
            assessment.saveFieldValue(CrfAssessmentTestFixtures.emptyFieldValue("f2"), 1L);

            assertThat(assessment.isFieldFilled("f1")).isTrue();
            assertThat(assessment.isFieldFilled("f2")).isFalse();
            assertThat(assessment.isFieldFilled("nonexistent")).isFalse();
        }

        /**
         * 验证getFieldValues()返回不可修改的列表，
         * 尝试添加元素时抛出UnsupportedOperationException
         */
        @Test
        @DisplayName("getFieldValues 返回不可修改的列表")
        void getFieldValuesReturnsUnmodifiableList() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assessment.saveFieldValue(CrfAssessmentTestFixtures.fieldValue("f1", "v1"), 1L);

            List<CrfFieldValue> fieldValues = assessment.getFieldValues();
            assertThatThrownBy(() -> fieldValues.add(
                    CrfAssessmentTestFixtures.fieldValue("f2", "v2")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * 验证setAdverseEvent()正确设置不良事件标志，
         * 默认isAdverseEvent()为false，设置为true后返回true
         */
        @Test
        @DisplayName("setAdverseEvent 正确设置不良事件标志")
        void setAdverseEventWorks() {
            CrfAssessment assessment = CrfAssessmentTestFixtures.aPendingAssessment();
            assertThat(assessment.isAdverseEvent()).isFalse();

            assessment.setAdverseEvent(true);
            assertThat(assessment.isAdverseEvent()).isTrue();
        }
    }
}
