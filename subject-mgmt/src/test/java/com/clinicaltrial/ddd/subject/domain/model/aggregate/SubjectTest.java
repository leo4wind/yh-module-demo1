package com.clinicaltrial.ddd.subject.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.subject.domain.event.SubjectCompletedEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectEnrolledEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectScreenedEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectStatusChangedEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectWithdrawnEvent;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive unit tests for {@link Subject}.
 * <p>
 * Covers all lifecycle state transitions, factory methods, code assignment,
 * and full end-to-end lifecycle scenarios. Pure domain tests — no mocks.
 * </p>
 */
class SubjectTest {

    // ===============================================================
    // Factory Tests
    // ===============================================================

    @Nested
    @DisplayName("Factory methods")
    class FactoryTests {

        /**
         * 验证createForEnrollment()创建的新受试者状态为null，受试者编号为null，
         * 分组ID列表为空，且不产生任何领域事件
         */
        @Test
        @DisplayName("createForEnrollment sets status to null and does not register events")
        void createForEnrollment() {
            Subject subject = SubjectTestFixtures.subjectForScreening();

            assertThat(subject.getId()).isNull();
            assertThat(subject.getStatus()).isNull();
            assertThat(subject.getProjectId()).isNotNull();
            assertThat(subject.getUserId()).isEqualTo(1L);
            assertThat(subject.getSiteId()).isEqualTo(1L);
            assertThat(subject.getBlh()).isEqualTo("BL001");
            assertThat(subject.getSyxh()).isEqualTo("SY001");
            assertThat(subject.getGroupSubsetIds()).isEmpty();
            assertThat(subject.getCode()).isNull();
            assertThat(subject.pullDomainEvents()).isEmpty();
        }

        /**
         * 验证createForEnrollment()传入null projectId时抛出IllegalArgumentException，
         * 提示信息包含"projectId must not be null"
         */
        @Test
        @DisplayName("createForEnrollment with null projectId throws IllegalArgumentException")
        void createForEnrollmentNullProjectId() {
            assertThatThrownBy(() ->
                    Subject.createForEnrollment(null, 1L, 1L, "BL001", "SY001", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("projectId must not be null");
        }

        /**
         * 验证createForEnrollment()传入null groupSubsetIds时，
         * 创建的受试者分组ID列表为空而非null
         */
        @Test
        @DisplayName("createForEnrollment with null groupSubsetIds creates empty list")
        void createForEnrollmentNullGroupSubsetIds() {
            Subject subject = Subject.createForEnrollment(
                    SubjectTestFixtures.aProjectId(), 1L, 1L, "BL001", "SY001", null);

            assertThat(subject.getGroupSubsetIds()).isEmpty();
        }

        /**
         * 验证reconstruct()正确恢复受试者的所有字段值，包括ID、状态、编号、
         * 筛选信息、退出原因等，且不产生任何领域事件
         */
        @Test
        @DisplayName("reconstruct restores status and does not register events")
        void reconstruct() {
            Subject subject = Subject.reconstruct(
                    SubjectTestFixtures.aSubjectId(),
                    SubjectTestFixtures.aProjectId(),
                    SubjectTestFixtures.aSubjectCode(),
                    SubjectStatus.ACTIVE,
                    1L, 2L, "BL001", "SY001",
                    java.util.Collections.singletonList("GRP1"),
                    SubjectTestFixtures.screeningPassed(),
                    null, "Remarks", null, null);

            assertThat(subject.getId()).isEqualTo(SubjectTestFixtures.aSubjectId());
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ACTIVE);
            assertThat(subject.getCode()).isEqualTo(SubjectTestFixtures.aSubjectCode());
            assertThat(subject.getUserId()).isEqualTo(1L);
            assertThat(subject.getSiteId()).isEqualTo(2L);
            assertThat(subject.getBlh()).isEqualTo("BL001");
            assertThat(subject.getSyxh()).isEqualTo("SY001");
            assertThat(subject.getGroupSubsetIds()).containsExactly("GRP1");
            assertThat(subject.getScreeningInfo()).isNotNull();
            assertThat(subject.getFallOffReason()).isNull();
            assertThat(subject.getRemarks()).isEqualTo("Remarks");
            assertThat(subject.pullDomainEvents()).isEmpty();
        }
    }

    // ===============================================================
    // Screening Transitions
    // ===============================================================

    @Nested
    @DisplayName("Screening transitions")
    class ScreeningTransitions {

        /**
         * 验证screen()将受试者从null状态流转为SCREENING状态，
         * 筛选信息被正确设置，并发布SubjectScreenedEvent
         */
        @Test
        @DisplayName("screen transitions null to SCREENING and raises SubjectScreenedEvent")
        void screenTransitionsToScreening() {
            Subject subject = SubjectTestFixtures.subjectForScreening();

            subject.screen(SubjectTestFixtures.screeningPassed());

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.SCREENING);
            assertThat(subject.getScreeningInfo()).isNotNull();
            assertThat(subject.getScreeningInfo().getScreeningResult())
                    .isEqualTo(com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo.ScreeningResult.PASS);

            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectScreenedEvent.class);
        }

        /**
         * 验证已处于SCREENING状态的受试者无法再次调用screen()，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_ALREADY_SCREENED
         */
        @Test
        @DisplayName("screen when already screened throws BusinessRuleViolationException")
        void screenWhenAlreadyScreened() {
            Subject subject = SubjectTestFixtures.screenedSubject();

            assertThatThrownBy(() -> subject.screen(SubjectTestFixtures.screeningPassed()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_ALREADY_SCREENED");

            // Status should remain SCREENING
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.SCREENING);
        }

        /**
         * 验证screen()传入null ScreeningInfo时抛出IllegalArgumentException，
         * 提示信息包含"ScreeningInfo must not be null"
         */
        @Test
        @DisplayName("screen with null info throws IllegalArgumentException")
        void screenWithNullInfo() {
            Subject subject = SubjectTestFixtures.subjectForScreening();

            assertThatThrownBy(() -> subject.screen(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ScreeningInfo must not be null");
        }
    }

    // ===============================================================
    // Enrollment Transitions
    // ===============================================================

    @Nested
    @DisplayName("Enrollment transitions")
    class EnrollmentTransitions {

        /**
         * 验证enroll()将受试者从null状态直接入组为ENROLLED状态，
         * 并发布SubjectEnrolledEvent
         */
        @Test
        @DisplayName("enroll from null (direct enrollment) transitions to ENROLLED and raises SubjectEnrolledEvent")
        void enrollFromNull() {
            Subject subject = SubjectTestFixtures.subjectForScreening();

            subject.enroll();

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ENROLLED);
            assertThat(subject.pullDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectEnrolledEvent.class);
        }

        /**
         * 验证enroll()将受试者从SCREENING状态入组为ENROLLED状态，
         * 并发布SubjectEnrolledEvent
         */
        @Test
        @DisplayName("enroll from SCREENING transitions to ENROLLED and raises SubjectEnrolledEvent")
        void enrollFromScreening() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            subject.pullDomainEvents(); // clear previous events

            subject.enroll();

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ENROLLED);
            assertThat(subject.pullDomainEvents())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectEnrolledEvent.class);
        }

        /**
         * 验证已激活(ACTIVE)状态的受试者无法再次入组，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_ENROLL
         */
        @Test
        @DisplayName("enroll from ACTIVE throws BusinessRuleViolationException")
        void enrollFromActive() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents(); // clear previous events

            assertThatThrownBy(subject::enroll)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_ENROLL");

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ACTIVE);
        }

        /**
         * 验证已完成(COMPLETED)状态的受试者无法入组，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_ENROLL
         */
        @Test
        @DisplayName("enroll from COMPLETED throws BusinessRuleViolationException")
        void enrollFromCompleted() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents(); // clear previous events
            subject.complete();
            subject.pullDomainEvents(); // clear completion event

            assertThatThrownBy(subject::enroll)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_ENROLL");
        }

        /**
         * 验证已终止(TERMINATED)状态的受试者无法入组，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_ENROLL
         */
        @Test
        @DisplayName("enroll from TERMINATED throws BusinessRuleViolationException")
        void enrollFromTerminated() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents(); // clear previous events
            subject.terminate(SubjectTestFixtures.aReason());
            subject.pullDomainEvents(); // clear termination event

            assertThatThrownBy(subject::enroll)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_ENROLL");
        }

        /**
         * 验证已退出(WITHDRAWN)状态的受试者无法入组，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_ENROLL
         */
        @Test
        @DisplayName("enroll from WITHDRAWN throws BusinessRuleViolationException")
        void enrollFromWithdrawn() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            subject.pullDomainEvents(); // clear previous events
            subject.withdraw(SubjectTestFixtures.aReason());
            subject.pullDomainEvents(); // clear withdrawal event

            assertThatThrownBy(subject::enroll)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_ENROLL");
        }
    }

    // ===============================================================
    // canEnroll queries
    // ===============================================================

    @Nested
    @DisplayName("canEnroll query")
    class CanEnrollTests {

        /**
         * 验证null状态（新创建未操作）的受试者canEnroll()返回true
         */
        @Test
        @DisplayName("canEnroll returns true for null status (fresh subject)")
        void canEnrollTrueForNullStatus() {
            Subject subject = SubjectTestFixtures.subjectForScreening();
            assertThat(subject.canEnroll()).isTrue();
        }

        /**
         * 验证SCREENING状态的受试者canEnroll()返回true，允许入组
         */
        @Test
        @DisplayName("canEnroll returns true for SCREENING status")
        void canEnrollTrueForScreening() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            assertThat(subject.canEnroll()).isTrue();
        }

        /**
         * 验证ENROLLED状态的受试者canEnroll()返回false，不可重复入组
         */
        @Test
        @DisplayName("canEnroll returns false for ENROLLED status")
        void canEnrollFalseForEnrolled() {
            Subject subject = SubjectTestFixtures.enrolledSubject();
            assertThat(subject.canEnroll()).isFalse();
        }

        /**
         * 验证ACTIVE状态的受试者canEnroll()返回false，不可入组
         */
        @Test
        @DisplayName("canEnroll returns false for ACTIVE status")
        void canEnrollFalseForActive() {
            Subject subject = SubjectTestFixtures.activeSubject();
            assertThat(subject.canEnroll()).isFalse();
        }

        /**
         * 验证COMPLETED状态的受试者canEnroll()返回false，不可入组
         */
        @Test
        @DisplayName("canEnroll returns false for COMPLETED status")
        void canEnrollFalseForCompleted() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();
            subject.complete();
            assertThat(subject.canEnroll()).isFalse();
        }

        /**
         * 验证TERMINATED状态的受试者canEnroll()返回false，不可入组
         */
        @Test
        @DisplayName("canEnroll returns false for TERMINATED status")
        void canEnrollFalseForTerminated() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();
            subject.terminate(SubjectTestFixtures.aReason());
            assertThat(subject.canEnroll()).isFalse();
        }

        /**
         * 验证WITHDRAWN状态的受试者canEnroll()返回false，不可入组
         */
        @Test
        @DisplayName("canEnroll returns false for WITHDRAWN status")
        void canEnrollFalseForWithdrawn() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            subject.pullDomainEvents();
            subject.withdraw(SubjectTestFixtures.aReason());
            assertThat(subject.canEnroll()).isFalse();
        }
    }

    // ===============================================================
    // Activation Transitions
    // ===============================================================

    @Nested
    @DisplayName("Activation transitions")
    class ActivationTransitions {

        /**
         * 验证activate()将受试者从ENROLLED状态激活为ACTIVE状态，
         * 并发布SubjectStatusChangedEvent，其中oldStatus为ENROLLED，newStatus为ACTIVE
         */
        @Test
        @DisplayName("activate transitions ENROLLED to ACTIVE and raises SubjectStatusChangedEvent")
        void activateFromEnrolled() {
            Subject subject = SubjectTestFixtures.enrolledSubject();
            subject.pullDomainEvents(); // clear enrollment events

            subject.activate();

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ACTIVE);

            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectStatusChangedEvent.class);

            SubjectStatusChangedEvent statusEvent =
                    (SubjectStatusChangedEvent) events.get(0);
            assertThat(statusEvent.getOldStatus()).isEqualTo(SubjectStatus.ENROLLED);
            assertThat(statusEvent.getNewStatus()).isEqualTo(SubjectStatus.ACTIVE);
        }

        /**
         * 验证SCREENING状态的受试者无法激活，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_ACTIVATE
         */
        @Test
        @DisplayName("activate from SCREENING throws BusinessRuleViolationException")
        void activateFromScreening() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(subject::activate)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_ACTIVATE");
        }

        /**
         * 验证ACTIVE状态的受试者无法重复激活，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_ACTIVATE
         */
        @Test
        @DisplayName("activate from ACTIVE throws BusinessRuleViolationException")
        void activateFromActive() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(subject::activate)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_ACTIVATE");
        }

        /**
         * 验证COMPLETED状态的受试者无法激活，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_ACTIVATE
         */
        @Test
        @DisplayName("activate from COMPLETED throws BusinessRuleViolationException")
        void activateFromCompleted() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();
            subject.complete();
            subject.pullDomainEvents();

            assertThatThrownBy(subject::activate)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_ACTIVATE");
        }
    }

    // ===============================================================
    // Completion Transitions
    // ===============================================================

    @Nested
    @DisplayName("Completion transitions")
    class CompletionTransitions {

        /**
         * 验证complete()将受试者从ACTIVE状态完成为COMPLETED状态，
         * 并发布SubjectCompletedEvent
         */
        @Test
        @DisplayName("complete transitions ACTIVE to COMPLETED and raises SubjectCompletedEvent")
        void completeFromActive() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents(); // clear previous events

            subject.complete();

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.COMPLETED);

            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectCompletedEvent.class);
        }

        /**
         * 验证SCREENING状态的受试者无法完成，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_COMPLETE
         */
        @Test
        @DisplayName("complete from SCREENING throws BusinessRuleViolationException")
        void completeFromScreening() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(subject::complete)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_COMPLETE");
        }

        /**
         * 验证ENROLLED状态的受试者无法完成，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_COMPLETE
         */
        @Test
        @DisplayName("complete from ENROLLED throws BusinessRuleViolationException")
        void completeFromEnrolled() {
            Subject subject = SubjectTestFixtures.enrolledSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(subject::complete)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_COMPLETE");
        }

        /**
         * 验证TERMINATED状态的受试者无法完成，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_COMPLETE
         */
        @Test
        @DisplayName("complete from TERMINATED throws BusinessRuleViolationException")
        void completeFromTerminated() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();
            subject.terminate(SubjectTestFixtures.aReason());
            subject.pullDomainEvents();

            assertThatThrownBy(subject::complete)
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_COMPLETE");
        }
    }

    // ===============================================================
    // Termination Transitions
    // ===============================================================

    @Nested
    @DisplayName("Termination transitions")
    class TerminationTransitions {

        /**
         * 验证terminate()将受试者从ACTIVE状态终止为TERMINATED状态，
         * 设置退出原因，isDiscontinued()返回true，并发布SubjectStatusChangedEvent
         */
        @Test
        @DisplayName("terminate transitions ACTIVE to TERMINATED and raises SubjectStatusChangedEvent")
        void terminateFromActive() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents(); // clear previous events

            subject.terminate(SubjectTestFixtures.aReason());

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.TERMINATED);
            assertThat(subject.getFallOffReason()).isNotNull();
            assertThat(subject.isDiscontinued()).isTrue();

            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectStatusChangedEvent.class);

            SubjectStatusChangedEvent statusEvent =
                    (SubjectStatusChangedEvent) events.get(0);
            assertThat(statusEvent.getOldStatus()).isEqualTo(SubjectStatus.ACTIVE);
            assertThat(statusEvent.getNewStatus()).isEqualTo(SubjectStatus.TERMINATED);
            assertThat(statusEvent.getReason()).contains("Adverse Event");
        }

        /**
         * 验证terminate()传入null SubjectFallOffReason时抛出IllegalArgumentException，
         * 提示信息包含"SubjectFallOffReason must not be null"
         */
        @Test
        @DisplayName("terminate with null reason throws IllegalArgumentException")
        void terminateWithNullReason() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(() -> subject.terminate(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SubjectFallOffReason must not be null");
        }

        /**
         * 验证SCREENING状态的受试者无法终止，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_TERMINATE
         */
        @Test
        @DisplayName("terminate from SCREENING throws BusinessRuleViolationException")
        void terminateFromScreening() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(() -> subject.terminate(SubjectTestFixtures.aReason()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_TERMINATE");
        }

        /**
         * 验证ENROLLED状态的受试者无法终止，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_TERMINATE
         */
        @Test
        @DisplayName("terminate from ENROLLED throws BusinessRuleViolationException")
        void terminateFromEnrolled() {
            Subject subject = SubjectTestFixtures.enrolledSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(() -> subject.terminate(SubjectTestFixtures.aReason()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_TERMINATE");
        }

        /**
         * 验证COMPLETED状态的受试者无法终止，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_TERMINATE
         */
        @Test
        @DisplayName("terminate from COMPLETED throws BusinessRuleViolationException")
        void terminateFromCompleted() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();
            subject.complete();
            subject.pullDomainEvents();

            assertThatThrownBy(() -> subject.terminate(SubjectTestFixtures.aReason()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_TERMINATE");
        }
    }

    // ===============================================================
    // Withdrawal Transitions
    // ===============================================================

    @Nested
    @DisplayName("Withdrawal transitions")
    class WithdrawalTransitions {

        /**
         * 验证withdraw()将受试者从SCREENING状态退出为WITHDRAWN状态，
         * 设置退出原因，并发布SubjectWithdrawnEvent
         */
        @Test
        @DisplayName("withdraw from SCREENING transitions to WITHDRAWN and raises SubjectWithdrawnEvent")
        void withdrawFromScreening() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            subject.pullDomainEvents(); // clear screening events

            subject.withdraw(SubjectTestFixtures.aReason());

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.WITHDRAWN);
            assertThat(subject.getFallOffReason()).isNotNull();
            assertThat(subject.isDiscontinued()).isTrue();

            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectWithdrawnEvent.class);
        }

        /**
         * 验证withdraw()将受试者从ACTIVE状态退出为WITHDRAWN状态，
         * isDiscontinued()返回true，并发布SubjectWithdrawnEvent
         */
        @Test
        @DisplayName("withdraw from ACTIVE transitions to WITHDRAWN and raises SubjectWithdrawnEvent")
        void withdrawFromActive() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents(); // clear previous events

            subject.withdraw(SubjectTestFixtures.aReason());

            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.WITHDRAWN);
            assertThat(subject.isDiscontinued()).isTrue();

            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectWithdrawnEvent.class);
        }

        /**
         * 验证ENROLLED状态的受试者无法退出，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_WITHDRAW
         */
        @Test
        @DisplayName("withdraw from ENROLLED throws BusinessRuleViolationException")
        void withdrawFromEnrolled() {
            Subject subject = SubjectTestFixtures.enrolledSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(() -> subject.withdraw(SubjectTestFixtures.aReason()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_WITHDRAW");
        }

        /**
         * 验证COMPLETED状态的受试者无法退出，
         * 抛出BusinessRuleViolationException，错误码为SUBJECT_CANNOT_WITHDRAW
         */
        @Test
        @DisplayName("withdraw from COMPLETED throws BusinessRuleViolationException")
        void withdrawFromCompleted() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();
            subject.complete();
            subject.pullDomainEvents();

            assertThatThrownBy(() -> subject.withdraw(SubjectTestFixtures.aReason()))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_WITHDRAW");
        }

        /**
         * 验证withdraw()传入null reason时抛出IllegalArgumentException，
         * 提示信息包含"SubjectFallOffReason must not be null"
         */
        @Test
        @DisplayName("withdraw with null reason throws IllegalArgumentException")
        void withdrawWithNullReason() {
            Subject subject = SubjectTestFixtures.screenedSubject();
            subject.pullDomainEvents();

            assertThatThrownBy(() -> subject.withdraw(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SubjectFallOffReason must not be null");
        }
    }

    // ===============================================================
    // Subject Code Assignment
    // ===============================================================

    @Nested
    @DisplayName("Subject code assignment")
    class SubjectCodeAssignment {

        /**
         * 验证assignCode()正确设置受试者编号，编号完整格式为"TEST-0001"
         */
        @Test
        @DisplayName("assignCode sets the subject code")
        void assignCodeSetsCode() {
            Subject subject = SubjectTestFixtures.subjectForScreening();
            SubjectCode code = SubjectTestFixtures.aSubjectCode();

            subject.assignCode(code);

            assertThat(subject.getCode()).isEqualTo(code);
            assertThat(subject.getCode().getFullCode()).isEqualTo("TEST-0001");
        }

        /**
         * 验证已分配编号的受试者无法重复分配，
         * 抛出IllegalStateException，提示信息包含"already has a code"
         */
        @Test
        @DisplayName("assignCode when already assigned throws IllegalStateException")
        void assignCodeWhenAlreadyAssigned() {
            Subject subject = SubjectTestFixtures.enrolledSubject();

            assertThatThrownBy(() -> subject.assignCode(SubjectTestFixtures.aSubjectCode()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already has a code");
        }

        /**
         * 验证assignCode()传入null SubjectCode时抛出IllegalArgumentException，
         * 提示信息包含"SubjectCode must not be null"
         */
        @Test
        @DisplayName("assignCode with null throws IllegalArgumentException")
        void assignCodeWithNull() {
            Subject subject = SubjectTestFixtures.subjectForScreening();

            assertThatThrownBy(() -> subject.assignCode(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SubjectCode must not be null");
        }
    }

    // ===============================================================
    // Full Lifecycle Scenarios
    // ===============================================================

    @Nested
    @DisplayName("Full lifecycle scenarios")
    class FullLifecycle {

        /**
         * 验证完整生命周期screen→enroll→assignCode→activate→complete流程，
         * 每个步骤状态变更正确，最终发布4个领域事件（顺序：Screened→Enrolled→StatusChanged→Completed）
         */
        @Test
        @DisplayName("screen -> enroll -> assignCode -> activate -> complete produces correct event sequence")
        void fullLifecycleWithScreening() {
            Subject subject = SubjectTestFixtures.subjectForScreening();

            // --- screen ---
            subject.screen(SubjectTestFixtures.screeningPassed());
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.SCREENING);

            // --- enroll ---
            subject.enroll();
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ENROLLED);

            // --- assign code ---
            subject.assignCode(SubjectTestFixtures.aSubjectCode());

            // --- activate ---
            subject.activate();
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ACTIVE);

            // --- complete ---
            subject.complete();
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.COMPLETED);

            // Verify all events in order
            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events).hasSize(4);
            assertThat(events.get(0)).isInstanceOf(SubjectScreenedEvent.class);
            assertThat(events.get(1)).isInstanceOf(SubjectEnrolledEvent.class);
            assertThat(events.get(2)).isInstanceOf(SubjectStatusChangedEvent.class);

            SubjectStatusChangedEvent activateEvent =
                    (SubjectStatusChangedEvent) events.get(2);
            assertThat(activateEvent.getOldStatus()).isEqualTo(SubjectStatus.ENROLLED);
            assertThat(activateEvent.getNewStatus()).isEqualTo(SubjectStatus.ACTIVE);

            assertThat(events.get(3)).isInstanceOf(SubjectCompletedEvent.class);
        }

        /**
         * 验证跳过筛选步骤直接入组(enroll)→激活(activate)→终止(terminate)的完整流程，
         * 事件序列为：Enrolled→StatusChanged→StatusChanged，触发终止时isDiscontinued()为true
         */
        @Test
        @DisplayName("direct enroll (null -> ENROLLED) -> activate -> terminate produces correct events")
        void directEnrollThenActivateThenTerminate() {
            Subject subject = SubjectTestFixtures.subjectForScreening();

            // --- direct enroll (skipping screening) ---
            subject.enroll();
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ENROLLED);

            // --- assign code ---
            subject.assignCode(SubjectTestFixtures.aSubjectCode());

            // --- activate ---
            subject.activate();
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.ACTIVE);

            // --- terminate ---
            subject.terminate(SubjectTestFixtures.aReason());
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.TERMINATED);
            assertThat(subject.isDiscontinued()).isTrue();

            // Verify events: enroll -> activate -> terminate
            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events).hasSize(3);
            assertThat(events.get(0)).isInstanceOf(SubjectEnrolledEvent.class);
            assertThat(events.get(1)).isInstanceOf(SubjectStatusChangedEvent.class);

            SubjectStatusChangedEvent activateEvent =
                    (SubjectStatusChangedEvent) events.get(1);
            assertThat(activateEvent.getOldStatus()).isEqualTo(SubjectStatus.ENROLLED);
            assertThat(activateEvent.getNewStatus()).isEqualTo(SubjectStatus.ACTIVE);

            assertThat(events.get(2)).isInstanceOf(SubjectStatusChangedEvent.class);
            SubjectStatusChangedEvent terminateEvent =
                    (SubjectStatusChangedEvent) events.get(2);
            assertThat(terminateEvent.getOldStatus()).isEqualTo(SubjectStatus.ACTIVE);
            assertThat(terminateEvent.getNewStatus()).isEqualTo(SubjectStatus.TERMINATED);
        }

        /**
         * 验证筛选后退出(screen→withdraw)的短生命周期，
         * 清理筛选事件后只产生一个SubjectWithdrawnEvent
         */
        @Test
        @DisplayName("screen -> withdraw produces single SubjectWithdrawnEvent")
        void screenThenWithdraw() {
            Subject subject = SubjectTestFixtures.subjectForScreening();

            subject.screen(SubjectTestFixtures.screeningPassed());
            subject.pullDomainEvents(); // clear screening event

            subject.withdraw(SubjectTestFixtures.aReason());
            assertThat(subject.getStatus()).isEqualTo(SubjectStatus.WITHDRAWN);

            List<com.clinicaltrial.ddd.common.model.DomainEvent> events = subject.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(SubjectWithdrawnEvent.class);
        }
    }

    // ===============================================================
    // Query methods
    // ===============================================================

    @Nested
    @DisplayName("Query methods")
    class QueryTests {

        /**
         * 验证isActive()对ACTIVE状态的受试者返回true
         */
        @Test
        @DisplayName("isActive returns true for ACTIVE status")
        void isActiveTrue() {
            Subject subject = SubjectTestFixtures.activeSubject();
            assertThat(subject.isActive()).isTrue();
        }

        /**
         * 验证isActive()对null状态、SCREENING状态、ENROLLED状态均返回false
         */
        @Test
        @DisplayName("isActive returns false for non-ACTIVE status")
        void isActiveFalse() {
            assertThat(SubjectTestFixtures.subjectForScreening().isActive()).isFalse();
            assertThat(SubjectTestFixtures.screenedSubject().isActive()).isFalse();
            assertThat(SubjectTestFixtures.enrolledSubject().isActive()).isFalse();
        }

        /**
         * 验证isCompleted()对COMPLETED状态的受试者返回true
         */
        @Test
        @DisplayName("isCompleted returns true for COMPLETED status")
        void isCompletedTrue() {
            Subject subject = SubjectTestFixtures.activeSubject();
            subject.pullDomainEvents();
            subject.complete();
            assertThat(subject.isCompleted()).isTrue();
        }

        /**
         * 验证isDiscontinued()对TERMINATED和WITHDRAWN状态的受试者均返回true，
         * 表示受试者已提前退出试验
         */
        @Test
        @DisplayName("isDiscontinued returns true for TERMINATED and WITHDRAWN")
        void isDiscontinuedTrue() {
            Subject terminated = SubjectTestFixtures.activeSubject();
            terminated.pullDomainEvents();
            terminated.terminate(SubjectTestFixtures.aReason());
            assertThat(terminated.isDiscontinued()).isTrue();

            Subject withdrawn = SubjectTestFixtures.activeSubject();
            withdrawn.pullDomainEvents();
            withdrawn.withdraw(SubjectTestFixtures.aReason());
            assertThat(withdrawn.isDiscontinued()).isTrue();
        }

        /**
         * 验证isDiscontinued()对正常ACTIVE状态的受试者返回false
         */
        @Test
        @DisplayName("isDiscontinued returns false for ACTIVE status")
        void isDiscontinuedFalseForActive() {
            Subject subject = SubjectTestFixtures.activeSubject();
            assertThat(subject.isDiscontinued()).isFalse();
        }
    }
}
