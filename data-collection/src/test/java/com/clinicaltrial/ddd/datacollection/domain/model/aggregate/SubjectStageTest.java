package com.clinicaltrial.ddd.datacollection.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.StageCompletedEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.StageEventGeneratedEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive unit tests for {@link SubjectStage}.
 * <p>
 * Covers all lifecycle state transitions, factory methods, CRF assessment reference
 * management, and completion validation. Pure domain tests — no mocks.
 * </p>
 */
@DisplayName("SubjectStage 状态机测试")
class SubjectStageTest {

    // ===============================================================
    // Factory Tests
    // ===============================================================

    @Nested
    @DisplayName("工厂方法")
    class FactoryTests {

        /**
         * 验证通过create()创建的SubjectStage初始状态为PENDING，
         * 开始/结束时间为null，CRF评估引用为空，并触发StageEventGeneratedEvent
         */
        @Test
        @DisplayName("create 创建 PENDING 状态的阶段，触发 StageEventGeneratedEvent")
        void createCreatesPendingStage() {
            SubjectStage stage = SubjectStageTestFixtures.aPendingStage();

            assertThat(stage.getId()).isEqualTo(SubjectStageTestFixtures.aSubjectStageId());
            assertThat(stage.getSubjectsUserId()).isEqualTo(SubjectStageTestFixtures.aSubjectId());
            assertThat(stage.getStageId()).isEqualTo(SubjectStageTestFixtures.aTrialStageId());
            assertThat(stage.getPlanEventId()).isEqualTo(SubjectStageTestFixtures.aPlanEventId());
            assertThat(stage.getBaselineTime()).isEqualTo(SubjectStageTestFixtures.aBaselineTime());
            assertThat(stage.getStatus()).isEqualTo(SubjectStageStatus.PENDING);
            assertThat(stage.getStageStartAt()).isNull();
            assertThat(stage.getStageEndAt()).isNull();
            assertThat(stage.getCompleteTime()).isNull();
            assertThat(stage.getCompleteUserId()).isNull();
            assertThat(stage.getCrfAssessmentRefs()).isEmpty();

            List<DomainEvent> events = stage.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(StageEventGeneratedEvent.class);

            StageEventGeneratedEvent event = (StageEventGeneratedEvent) events.get(0);
            assertThat(event.getSubjectStageId()).isEqualTo(SubjectStageTestFixtures.aSubjectStageId());
            assertThat(event.getSubjectId()).isEqualTo(SubjectStageTestFixtures.aSubjectId());
            assertThat(event.getStageId()).isEqualTo(SubjectStageTestFixtures.aTrialStageId());
            assertThat(event.getPlanEventId()).isEqualTo(SubjectStageTestFixtures.aPlanEventId());
        }

        /**
         * 验证reconstruct()正确恢复SubjectStage的所有字段值，
         * 包括状态、完成信息、CRF引用等，且不产生领域事件
         */
        @Test
        @DisplayName("reconstruct 恢复所有字段，无领域事件")
        void reconstructRestoresState() {
            SubjectStage original = SubjectStageTestFixtures.aCompletedStage();

            SubjectStage reconstructed = SubjectStage.reconstruct(
                    original.getId(),
                    original.getSubjectsUserId(),
                    original.getStageId(),
                    original.getPlanEventId(),
                    original.getStatus(),
                    original.getStageStartAt(),
                    original.getStageEndAt(),
                    original.getBaselineTime(),
                    original.getFollowUpPeriod(),
                    original.getFollowUpStatus(),
                    original.getCompleteTime(),
                    original.getCompleteUserId(),
                    original.getCrfAssessmentRefs()
            );

            assertThat(reconstructed.getId()).isEqualTo(original.getId());
            assertThat(reconstructed.getStatus()).isEqualTo(SubjectStageStatus.COMPLETED);
            assertThat(reconstructed.getCompleteUserId()).isEqualTo(100L);
            assertThat(reconstructed.getCompleteTime()).isNotNull();
            assertThat(reconstructed.getCrfAssessmentRefs()).isEmpty();
            assertThat(reconstructed.pullDomainEvents()).isEmpty();
        }
    }

    // ===============================================================
    // Start Tests
    // ===============================================================

    @Nested
    @DisplayName("开始阶段")
    class StartTests {

        /**
         * 验证start()将PENDING状态的阶段流转为IN_PROGRESS，
         * 设置开始时间且不产生领域事件
         */
        @Test
        @DisplayName("start: PENDING→IN_PROGRESS")
        void startTransitionsToInProgress() {
            SubjectStage stage = SubjectStageTestFixtures.aPendingStage();
            stage.pullDomainEvents(); // clear generation event

            stage.start();

            assertThat(stage.getStatus()).isEqualTo(SubjectStageStatus.IN_PROGRESS);
            assertThat(stage.getStageStartAt()).isNotNull();
            assertThat(stage.pullDomainEvents()).isEmpty(); // start does not register events
        }

        /**
         * 验证IN_PROGRESS状态下再次start()被拒绝，
         * 抛出BusinessRuleViolationException，提示信息包含"PENDING"
         */
        @Test
        @DisplayName("IN_PROGRESS 状态下 start 抛出 BusinessRuleViolationException")
        void startWhenInProgressThrows() {
            SubjectStage stage = SubjectStageTestFixtures.aStartedStage();

            assertThatThrownBy(stage::start)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PENDING");
        }

        /**
         * 验证COMPLETED状态下start()被拒绝，已完成的阶段无法重新开始，
         * 抛出BusinessRuleViolationException
         */
        @Test
        @DisplayName("COMPLETED 状态下 start 抛出 BusinessRuleViolationException")
        void startWhenCompletedThrows() {
            SubjectStage stage = SubjectStageTestFixtures.aCompletedStage();

            assertThatThrownBy(stage::start)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("PENDING");
        }
    }

    // ===============================================================
    // Complete Tests
    // ===============================================================

    @Nested
    @DisplayName("完成阶段")
    class CompleteTests {

        /**
         * 验证所有CRF评估引用都完成时，阶段从IN_PROGRESS流转为COMPLETED，
         * 设置完成用户ID和完成时间，触发StageCompletedEvent
         */
        @Test
        @DisplayName("所有引用评估完成时 IN_PROGRESS→COMPLETED，触发 StageCompletedEvent")
        void completeWhenAllRefsDone() {
            SubjectStage stage = SubjectStageTestFixtures.aStartedStage();
            stage.pullDomainEvents();

            // Add CRF assessment refs
            CrfAssessmentId ref1 = SubjectStageTestFixtures.aCrfAssessmentId(1L);
            CrfAssessmentId ref2 = SubjectStageTestFixtures.aCrfAssessmentId(2L);
            stage.addCrfAssessmentRef(ref1);
            stage.addCrfAssessmentRef(ref2);

            // Complete with all refs in the set
            Set<CrfAssessmentId> completedIds = new HashSet<>();
            completedIds.add(ref1);
            completedIds.add(ref2);
            stage.complete(completedIds, 200L);

            assertThat(stage.getStatus()).isEqualTo(SubjectStageStatus.COMPLETED);
            assertThat(stage.getCompleteUserId()).isEqualTo(200L);
            assertThat(stage.getCompleteTime()).isNotNull();

            List<DomainEvent> events = stage.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(StageCompletedEvent.class);

            StageCompletedEvent event = (StageCompletedEvent) events.get(0);
            assertThat(event.getSubjectStageId()).isEqualTo(SubjectStageTestFixtures.aSubjectStageId());
            assertThat(event.getSubjectId()).isEqualTo(SubjectStageTestFixtures.aSubjectId());
            assertThat(event.getStageId()).isEqualTo(SubjectStageTestFixtures.aTrialStageId());
        }

        /**
         * 验证存在未完成的CRF评估引用时complete()被拒绝，
         * 抛出BusinessRuleViolationException，状态保持IN_PROGRESS不变
         */
        @Test
        @DisplayName("存在未完成的引用评估时抛出 BusinessRuleViolationException")
        void completeWhenRefsNotAllDoneThrows() {
            SubjectStage stage = SubjectStageTestFixtures.aStartedStage();
            stage.pullDomainEvents();

            stage.addCrfAssessmentRef(SubjectStageTestFixtures.aCrfAssessmentId(1L));
            stage.addCrfAssessmentRef(SubjectStageTestFixtures.aCrfAssessmentId(2L));

            Set<CrfAssessmentId> completedIds = new HashSet<>();
            completedIds.add(SubjectStageTestFixtures.aCrfAssessmentId(1L));
            // ref 2 is missing

            assertThatThrownBy(() -> stage.complete(completedIds, 200L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("not all CRF assessments are completed");

            // Status should remain IN_PROGRESS
            assertThat(stage.getStatus()).isEqualTo(SubjectStageStatus.IN_PROGRESS);
        }

        /**
         * 验证PENDING状态下complete()被拒绝，提示信息包含"IN_PROGRESS"，
         * 阶段必须先开始才能完成
         */
        @Test
        @DisplayName("PENDING 状态下 complete 抛出 BusinessRuleViolationException")
        void completeWhenPendingThrows() {
            SubjectStage stage = SubjectStageTestFixtures.aPendingStage();
            stage.pullDomainEvents();

            assertThatThrownBy(() -> stage.complete(new HashSet<>(), 1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("IN_PROGRESS");
        }
    }

    // ===============================================================
    // CRF Assessment Ref Tests
    // ===============================================================

    @Nested
    @DisplayName("CRF评估引用管理")
    class CrfAssessmentRefTests {

        /**
         * 验证addCrfAssessmentRef()向阶段中添加CRF评估引用，
         * 引用列表大小变为1，引用ID正确
         */
        @Test
        @DisplayName("addCrfAssessmentRef 添加引用")
        void addCrfAssessmentRefAddsReference() {
            SubjectStage stage = SubjectStageTestFixtures.aStartedStage();
            stage.pullDomainEvents();

            CrfAssessmentId refId = SubjectStageTestFixtures.aCrfAssessmentId(42L);
            stage.addCrfAssessmentRef(refId);

            assertThat(stage.getCrfAssessmentRefs()).hasSize(1);
            assertThat(stage.getCrfAssessmentRefs().get(0).getAssessmentId())
                    .isEqualTo(refId);
        }

        /**
         * 验证COMPLETED状态下无法添加CRF评估引用，
         * 抛出BusinessRuleViolationException，提示信息包含"Cannot add CRF assessment ref"
         */
        @Test
        @DisplayName("COMPLETED 状态下 addCrfAssessmentRef 抛出 BusinessRuleViolationException")
        void addCrfAssessmentRefWhenCompletedThrows() {
            SubjectStage stage = SubjectStageTestFixtures.aCompletedStage();

            assertThatThrownBy(() ->
                    stage.addCrfAssessmentRef(
                            SubjectStageTestFixtures.aCrfAssessmentId(99L)))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("Cannot add CRF assessment ref");
        }

        /**
         * 验证重复添加同一CRF评估引用不会产生重复条目，
         * 引用列表大小仍然为1
         */
        @Test
        @DisplayName("重复添加同一引用不会导致重复条目")
        void addDuplicateRefDoesNotDuplicate() {
            SubjectStage stage = SubjectStageTestFixtures.aStartedStage();
            stage.pullDomainEvents();

            CrfAssessmentId refId = SubjectStageTestFixtures.aCrfAssessmentId(1L);
            stage.addCrfAssessmentRef(refId);
            stage.addCrfAssessmentRef(refId); // same ref again

            assertThat(stage.getCrfAssessmentRefs()).hasSize(1);
        }
    }

    // ===============================================================
    // isReadyToComplete Tests
    // ===============================================================

    @Nested
    @DisplayName("就绪状态检查")
    class IsReadyToCompleteTests {

        /**
         * 验证所有CRF评估引用都已完成时isReadyToComplete()返回true
         */
        @Test
        @DisplayName("所有引用评估完成时 isReadyToComplete 返回 true")
        void isReadyToCompleteWithAllRefs() {
            SubjectStage stage = SubjectStageTestFixtures.aStartedStage();
            stage.pullDomainEvents();

            stage.addCrfAssessmentRef(SubjectStageTestFixtures.aCrfAssessmentId(1L));
            stage.addCrfAssessmentRef(SubjectStageTestFixtures.aCrfAssessmentId(2L));

            Set<CrfAssessmentId> completedIds = new HashSet<>();
            completedIds.add(SubjectStageTestFixtures.aCrfAssessmentId(1L));
            completedIds.add(SubjectStageTestFixtures.aCrfAssessmentId(2L));

            assertThat(stage.isReadyToComplete(completedIds)).isTrue();
        }

        /**
         * 验证存在未完成的CRF评估引用时isReadyToComplete()返回false
         */
        @Test
        @DisplayName("存在缺失的引用评估时 isReadyToComplete 返回 false")
        void isReadyToCompleteWithMissingRef() {
            SubjectStage stage = SubjectStageTestFixtures.aStartedStage();
            stage.pullDomainEvents();

            stage.addCrfAssessmentRef(SubjectStageTestFixtures.aCrfAssessmentId(1L));
            stage.addCrfAssessmentRef(SubjectStageTestFixtures.aCrfAssessmentId(2L));

            Set<CrfAssessmentId> completedIds = new HashSet<>();
            completedIds.add(SubjectStageTestFixtures.aCrfAssessmentId(1L));
            // ref 2 is missing

            assertThat(stage.isReadyToComplete(completedIds)).isFalse();
        }

        /**
         * 验证没有CRF评估引用时isReadyToComplete()返回true，
         * 空引用列表视为全部完成
         */
        @Test
        @DisplayName("无引用时 isReadyToComplete 返回 true")
        void isReadyToCompleteWithNoRefs() {
            SubjectStage stage = SubjectStageTestFixtures.aStartedStage();
            stage.pullDomainEvents();

            assertThat(stage.isReadyToComplete(new HashSet<>())).isTrue();
        }
    }
}
