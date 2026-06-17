package com.clinicaltrial.ddd.subject.application;

import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.subject.application.service.SubjectApplicationService;
import com.clinicaltrial.ddd.subject.application.command.ChangeSubjectStatusCommand;
import com.clinicaltrial.ddd.subject.application.command.EnrollSubjectCommand;
import com.clinicaltrial.ddd.subject.application.command.ScreenSubjectCommand;
import com.clinicaltrial.ddd.subject.application.command.WithdrawSubjectCommand;
import com.clinicaltrial.ddd.subject.domain.event.SubjectEnrolledEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectScreenedEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectStatusChangedEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectWithdrawnEvent;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectFallOffReason;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;
import com.clinicaltrial.ddd.subject.domain.repository.SubjectRepository;
import com.clinicaltrial.ddd.subject.domain.service.SubjectCodeGenerator;
import com.clinicaltrial.ddd.subject.domain.service.SubjectEnrollmentDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SubjectApplicationService}.
 * <p>
 * Tests the orchestration layer: verifying that the application service correctly
 * loads/creates aggregates, invokes domain methods, persists via repository,
 * and publishes domain events. All dependencies are mocked.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class SubjectApplicationServiceTest {

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    SubjectEnrollmentDomainService enrollmentDomainService;

    @Mock
    SubjectCodeGenerator codeGenerator;

    @Mock
    EventBus eventBus;

    @InjectMocks
    SubjectApplicationService service;

    // ---------------------------------------------------------------
    // Shared fixtures
    // ---------------------------------------------------------------

    private static final ProjectId PROJECT_ID = new ProjectId(100L);
    private static final SubjectId SUBJECT_ID = new SubjectId(1L);
    private static final Long USER_ID = 1L;
    private static final Long SITE_ID = 1L;
    private static final SubjectCode SUBJECT_CODE = new SubjectCode("TEST", 1);

    /**
     * Creates a new Subject in SCREENING status (as if it came from the repository).
     */
    private Subject screenedSubject() {
        Subject s = Subject.createForEnrollment(PROJECT_ID, USER_ID, SITE_ID,
                "BL001", "SY001", Collections.emptyList());
        s.assignCode(SUBJECT_CODE);
        s.screen(new ScreeningInfo(LocalDate.of(2026, 6, 16),
                ScreeningInfo.ScreeningResult.PASS, "OK"));
        s.pullDomainEvents(); // clear screening event
        return s;
    }

    /**
     * Creates a new Subject in ACTIVE status (for withdraw/changeStatus tests).
     */
    private Subject activeSubject() {
        Subject s = screenedSubject();
        s.enroll();
        s.activate();
        s.pullDomainEvents(); // clear all events
        return s;
    }

    /**
     * Creates a new Subject in ENROLLED status.
     */
    private Subject enrolledSubject() {
        Subject s = screenedSubject();
        s.enroll();
        s.pullDomainEvents();
        return s;
    }

    @BeforeEach
    void setUp() {
        // Common mock: subjectRepository.save returns the argument as-is
        // Use lenient() because some error-case tests don't reach save()
        lenient().when(subjectRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    // ===============================================================
    // screenSubject Tests
    // ===============================================================

    @Nested
    @DisplayName("screenSubject")
    class ScreenSubjectTests {

        /**
         * 验证screenSubject用例的编排流程：创建Subject聚合→调用screen()领域方法→
         * 通过仓储保存→通过EventBus发布SubjectScreenedEvent
         */
        @Test
        @DisplayName("creates subject, calls screen, saves, and publishes SubjectScreenedEvent")
        void screenSubjectCreatesAndPublishes() {
            ScreenSubjectCommand command = new ScreenSubjectCommand(
                    PROJECT_ID, SITE_ID,
                    LocalDate.of(2026, 6, 16),
                    ScreeningInfo.ScreeningResult.PASS,
                    "Eligible", USER_ID, "BL001", "SY001");

            Subject result = service.screenSubject(command);

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.SCREENING);
            assertThat(result.getProjectId()).isEqualTo(PROJECT_ID);
            assertThat(result.getScreeningInfo()).isNotNull();
            assertThat(result.getScreeningInfo().getScreeningResult())
                    .isEqualTo(ScreeningInfo.ScreeningResult.PASS);

            verify(subjectRepository).save(any(Subject.class));
            verify(eventBus).publishAll(any(Subject.class));
        }

        /**
         * 验证screenSubject传入null command时抛出IllegalArgumentException
         */
        @Test
        @DisplayName("screenSubject with null command throws IllegalArgumentException")
        void screenSubjectNullCommand() {
            assertThatThrownBy(() -> service.screenSubject(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("command");
        }
    }

    // ===============================================================
    // enrollSubject (screen-then-enroll) Tests
    // ===============================================================

    @Nested
    @DisplayName("enrollSubject (screen-then-enroll)")
    class EnrollScreenedSubjectTests {

        /**
         * 验证enrollSubject筛选后入组用例：通过仓储加载已筛选的Subject→调用
         * enrollmentDomainService.enrollSubject()→通过仓储保存→通过EventBus发布SubjectEnrolledEvent
         */
        @Test
        @DisplayName("enrolls existing screened subject, saves, and publishes SubjectEnrolledEvent")
        void enrollScreenedSubject() {
            Subject screened = screenedSubject();
            when(subjectRepository.getById(SUBJECT_ID)).thenReturn(screened);

            // Mock the enrollment domain service to actually perform the enrollment
            doAnswer(invocation -> {
                Subject s = invocation.getArgument(0);
                s.enroll();
                return null;
            }).when(enrollmentDomainService).enrollSubject(any(), any(), anyBoolean());

            EnrollSubjectCommand command = new EnrollSubjectCommand(
                    SUBJECT_ID, PROJECT_ID, SITE_ID, USER_ID,
                    "BL001", "SY001", Collections.emptyList());

            Subject result = service.enrollSubject(command);

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.ENROLLED);
            verify(subjectRepository).getById(SUBJECT_ID);
            verify(enrollmentDomainService).enrollSubject(screened, PROJECT_ID, true);
            verify(subjectRepository).save(any(Subject.class));
            verify(eventBus).publishAll(any(Subject.class));
        }

        /**
         * 验证enrollSubject在已入组状态下抛出BusinessRuleViolationException，
         * 异常在调用仓储保存之前被传播，仓储save不会被调用
         */
        @Test
        @DisplayName("enrollSubject when already enrolled propagates BusinessRuleViolationException")
        void enrollSubjectAlreadyEnrolled() {
            Subject enrolled = enrolledSubject();
            when(subjectRepository.getById(SUBJECT_ID)).thenReturn(enrolled);

            doThrow(new BusinessRuleViolationException(
                    "SUBJECT_CANNOT_ENROLL",
                    "Subject cannot be enrolled from ENROLLED status"))
                    .when(enrollmentDomainService).enrollSubject(any(), any(), anyBoolean());

            EnrollSubjectCommand command = new EnrollSubjectCommand(
                    SUBJECT_ID, PROJECT_ID, SITE_ID, USER_ID,
                    "BL001", "SY001", Collections.emptyList());

            assertThatThrownBy(() -> service.enrollSubject(command))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("SUBJECT_CANNOT_ENROLL");

            // The exception should propagate before save is called
            verify(subjectRepository).getById(SUBJECT_ID);
            verify(enrollmentDomainService).enrollSubject(any(), any(), anyBoolean());
        }
    }

    // ===============================================================
    // enrollSubject (direct enrollment) Tests
    // ===============================================================

    @Nested
    @DisplayName("enrollSubject (direct enrollment)")
    class DirectEnrollSubjectTests {

        /**
         * 验证直接入组（跳过筛选）的用例编排流程：通过CodeGenerator生成受试者编号→
         * 创建Subject聚合→调用enroll()领域方法→通过仓储保存→通过EventBus发布SubjectEnrolledEvent
         */
        @Test
        @DisplayName("creates new subject, enrolls directly, saves, and publishes SubjectEnrolledEvent")
        void directEnrollSubject() {
            when(codeGenerator.generateNextCode(PROJECT_ID)).thenReturn(SUBJECT_CODE);

            EnrollSubjectCommand command = new EnrollSubjectCommand(
                    PROJECT_ID, SITE_ID, USER_ID,
                    "BL001", "SY001", Collections.emptyList());

            Subject result = service.enrollSubject(command);

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.ENROLLED);
            assertThat(result.getCode()).isEqualTo(SUBJECT_CODE);
            assertThat(result.getProjectId()).isEqualTo(PROJECT_ID);

            verify(codeGenerator).generateNextCode(PROJECT_ID);
            verify(subjectRepository).save(any(Subject.class));
            verify(eventBus).publishAll(any(Subject.class));
        }

        /**
         * 验证enrollSubject传入null command时抛出IllegalArgumentException
         */
        @Test
        @DisplayName("enrollSubject with null command throws IllegalArgumentException")
        void enrollSubjectNullCommand() {
            assertThatThrownBy(() -> service.enrollSubject(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("command");
        }
    }

    // ===============================================================
    // withdrawSubject Tests
    // ===============================================================

    @Nested
    @DisplayName("withdrawSubject")
    class WithdrawSubjectTests {

        /**
         * 验证withdrawSubject退出用例的编排流程：通过仓储加载Active状态的Subject→
         * 调用withdraw()领域方法→通过仓储保存→通过EventBus发布SubjectWithdrawnEvent
         */
        @Test
        @DisplayName("loads subject, withdraws, saves, and publishes SubjectWithdrawnEvent")
        void withdrawSubject() {
            Subject active = activeSubject();
            when(subjectRepository.getById(SUBJECT_ID)).thenReturn(active);

            WithdrawSubjectCommand command = new WithdrawSubjectCommand(
                    SUBJECT_ID, "WITHDRAWAL", "Voluntary withdrawal");

            Subject result = service.withdrawSubject(command);

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.WITHDRAWN);
            assertThat(result.getFallOffReason()).isNotNull();
            assertThat(result.getFallOffReason().getReasonCode()).isEqualTo("WITHDRAWAL");

            verify(subjectRepository).getById(SUBJECT_ID);
            verify(subjectRepository).save(any(Subject.class));
            verify(eventBus).publishAll(any(Subject.class));
        }

        /**
         * 验证withdrawSubject当受试者不存在时抛出AggregateNotFoundException，
         * 包含受试者ID的错误信息
         */
        @Test
        @DisplayName("withdrawSubject when subject not found throws AggregateNotFoundException")
        void withdrawSubjectNotFound() {
            when(subjectRepository.getById(SUBJECT_ID))
                    .thenThrow(new AggregateNotFoundException("Subject", SUBJECT_ID.getValue()));

            WithdrawSubjectCommand command = new WithdrawSubjectCommand(
                    SUBJECT_ID, "WITHDRAWAL", "Withdrew consent");

            assertThatThrownBy(() -> service.withdrawSubject(command))
                    .isInstanceOf(AggregateNotFoundException.class)
                    .hasMessageContaining("Subject")
                    .hasMessageContaining(SUBJECT_ID.getValue().toString());
        }

        /**
         * 验证withdrawSubject传入null command时抛出IllegalArgumentException
         */
        @Test
        @DisplayName("withdrawSubject with null command throws IllegalArgumentException")
        void withdrawSubjectNullCommand() {
            assertThatThrownBy(() -> service.withdrawSubject(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("command");
        }
    }

    // ===============================================================
    // changeStatus Tests
    // ===============================================================

    @Nested
    @DisplayName("changeStatus")
    class ChangeStatusTests {

        /**
         * 验证changeStatus将受试者状态变更为COMPLETED的编排流程：
         * 加载Subject→调用complete()领域方法→仓储保存→EventBus发布事件
         */
        @Test
        @DisplayName("changeStatus to COMPLETED loads, completes, saves, and publishes")
        void changeStatusToCompleted() {
            Subject active = activeSubject();
            when(subjectRepository.getById(SUBJECT_ID)).thenReturn(active);

            ChangeSubjectStatusCommand command = new ChangeSubjectStatusCommand(
                    SUBJECT_ID, SubjectStatus.COMPLETED, null);

            Subject result = service.changeStatus(command);

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.COMPLETED);
            verify(subjectRepository).save(any(Subject.class));
            verify(eventBus).publishAll(any(Subject.class));
        }

        /**
         * 验证changeStatus将受试者状态变更为ACTIVE的编排流程：
         * 加载ENROLLED状态的Subject→调用activate()→仓储保存→EventBus发布SubjectStatusChangedEvent，
         * 该事件包含旧状态ENROLLED和新状态ACTIVE
         */
        @Test
        @DisplayName("changeStatus to ACTIVE loads, activates, saves, and publishes SubjectStatusChangedEvent")
        void changeStatusToActive() {
            Subject enrolled = enrolledSubject();
            when(subjectRepository.getById(SUBJECT_ID)).thenReturn(enrolled);

            ChangeSubjectStatusCommand command = new ChangeSubjectStatusCommand(
                    SUBJECT_ID, SubjectStatus.ACTIVE, null);

            Subject result = service.changeStatus(command);

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.ACTIVE);

            // Capture and verify the domain event
            ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
            verify(eventBus).publishAll(subjectCaptor.capture());

            List<DomainEvent> events = subjectCaptor.getValue().pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(SubjectStatusChangedEvent.class);

            SubjectStatusChangedEvent statusEvent =
                    (SubjectStatusChangedEvent) events.get(0);
            assertThat(statusEvent.getOldStatus()).isEqualTo(SubjectStatus.ENROLLED);
            assertThat(statusEvent.getNewStatus()).isEqualTo(SubjectStatus.ACTIVE);
        }

        /**
         * 验证changeStatus将受试者状态变更为TERMINATED的编排流程：
         * 加载Active状态的Subject→调用terminate()并传入原因→仓储保存→EventBus发布事件
         */
        @Test
        @DisplayName("changeStatus to TERMINATED loads, terminates, saves, and publishes")
        void changeStatusToTerminated() {
            Subject active = activeSubject();
            when(subjectRepository.getById(SUBJECT_ID)).thenReturn(active);

            ChangeSubjectStatusCommand command = new ChangeSubjectStatusCommand(
                    SUBJECT_ID, SubjectStatus.TERMINATED, "AE");

            Subject result = service.changeStatus(command);

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.TERMINATED);
            assertThat(result.getFallOffReason()).isNotNull();
            assertThat(result.getFallOffReason().getReasonDescription()).contains("AE");
            verify(subjectRepository).save(any(Subject.class));
            verify(eventBus).publishAll(any(Subject.class));
        }

        /**
         * 验证changeStatus将受试者状态变更为WITHDRAWN的编排流程：
         * 加载Active状态的Subject→调用withdraw()→仓储保存→EventBus发布事件
         */
        @Test
        @DisplayName("changeStatus to WITHDRAWN loads, withdraws, saves, and publishes")
        void changeStatusToWithdrawn() {
            Subject active = activeSubject();
            when(subjectRepository.getById(SUBJECT_ID)).thenReturn(active);

            ChangeSubjectStatusCommand command = new ChangeSubjectStatusCommand(
                    SUBJECT_ID, SubjectStatus.WITHDRAWN, "Voluntary");

            Subject result = service.changeStatus(command);

            assertThat(result.getStatus()).isEqualTo(SubjectStatus.WITHDRAWN);
            verify(subjectRepository).save(any(Subject.class));
            verify(eventBus).publishAll(any(Subject.class));
        }

        /**
         * 验证changeStatus传入null command时抛出IllegalArgumentException
         */
        @Test
        @DisplayName("changeStatus with null command throws IllegalArgumentException")
        void changeStatusNullCommand() {
            assertThatThrownBy(() -> service.changeStatus(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("command");
        }

        /**
         * 验证changeStatus传入SCREENING等不支持直接变更的状态时抛出UnsupportedOperationException，
         * 提示信息包含"Cannot change subject status to SCREENING"
         */
        @Test
        @DisplayName("changeStatus to unsupported status throws UnsupportedOperationException")
        void changeStatusUnsupported() {
            ChangeSubjectStatusCommand command = new ChangeSubjectStatusCommand(
                    SUBJECT_ID, SubjectStatus.SCREENING, null);

            assertThatThrownBy(() -> service.changeStatus(command))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessageContaining("Cannot change subject status to SCREENING");
        }
    }

    // ===============================================================
    // Error propagation tests
    // ===============================================================

    @Nested
    @DisplayName("Error handling")
    class ErrorHandlingTests {

        /**
         * 验证异常回滚行为：当仓储save()抛出异常时，
         * EventBus.publishAll()不会被调用，确保事件在持久化失败时不被发布
         */
        @Test
        @DisplayName("when repository throws, eventBus.publishAll is NOT called")
        void repositoryExceptionSkipsPublish() {
            when(subjectRepository.save(any()))
                    .thenThrow(new RuntimeException("DB connection lost"));

            ScreenSubjectCommand command = new ScreenSubjectCommand(
                    PROJECT_ID, SITE_ID,
                    LocalDate.of(2026, 6, 16),
                    ScreeningInfo.ScreeningResult.PASS,
                    "OK", USER_ID, "BL001", "SY001");

            assertThatThrownBy(() -> service.screenSubject(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("DB connection lost");

            // eventBus.publishAll should NOT be called if save fails
            verify(eventBus, org.mockito.Mockito.never()).publishAll(any(Subject.class));
        }
    }
}
