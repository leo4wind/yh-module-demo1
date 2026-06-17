package com.clinicaltrial.ddd.query.application;

import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SnapshotValue;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.query.application.command.CloseQueryCommand;
import com.clinicaltrial.ddd.query.application.command.RaiseQueryCommand;
import com.clinicaltrial.ddd.query.application.command.ReopenQueryCommand;
import com.clinicaltrial.ddd.query.application.command.RespondToQueryCommand;
import com.clinicaltrial.ddd.query.application.service.QueryApplicationService;
import com.clinicaltrial.ddd.query.domain.event.QueryClosedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryRaisedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryReopenedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryRespondedEvent;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryType;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryUpdateType;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;
import com.clinicaltrial.ddd.query.domain.service.QueryDuplicateValidationService;
import com.clinicaltrial.ddd.query.domain.service.QueryLifecycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link QueryApplicationService}.
 * <p>
 * Tests the orchestration layer for query management use cases:
 * raise, respond, close, and reopen. All dependencies are mocked.
 * Verifies the pattern: load/create aggregate -> invoke domain method -> save -> publish events.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class QueryApplicationServiceTest {

    @Mock
    QueryRepository queryRepository;

    @Mock
    CrfAssessmentRepository assessmentRepository;

    @Mock
    QueryDuplicateValidationService duplicateValidationService;

    @Mock
    QueryLifecycleService queryLifecycleService;

    @Mock
    EventBus eventBus;

    @InjectMocks
    QueryApplicationService service;

    // ---------------------------------------------------------------
    // Shared fixtures
    // ---------------------------------------------------------------

    private static final CrfAssessmentId ASSESSMENT_ID = new CrfAssessmentId(100L);
    private static final String FIELD_CODE = "field1";
    private static final String FIELD_LABEL = "Field 1";
    private static final String FIELD_TYPE = "TEXT";

    @BeforeEach
    void setUp() {
        // Common mock: queryRepository.save returns the argument as-is
        // Use lenient() because error-case tests don't reach save()
        lenient().when(queryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    /**
     * Creates a mock CrfAssessment with the given field label lookup behavior.
     */
    private CrfAssessment mockAssessmentWithFieldLabel(String label) {
        CrfAssessment assessment = mock(CrfAssessment.class);
        CrfFieldValue fieldValue = mock(CrfFieldValue.class);
        when(fieldValue.getFieldLabel()).thenReturn(label);
        when(assessment.findFieldValueByCode(FIELD_CODE))
                .thenReturn(Optional.of(fieldValue));
        return assessment;
    }

    // ===============================================================
    // raiseQuery Tests
    // ===============================================================

    @Nested
    @DisplayName("raiseQuery")
    class RaiseQueryTests {

        /**
         * 验证raiseQuery()用例创建质疑、校验重复性、保存并发布QueryRaisedEvent(质疑发起事件)。
         * 前置条件：表单评估(assessment)和重复性校验服务(duplicateValidationService)模拟成功，
         * 执行raiseQuery()后质疑状态为OPEN(待回应)，类型为MONITOR_QUERY，
         * 校验无重复质疑、记录质疑生命周期、保存质疑和评估、发布事件等交互均被正确调用。
         */
        @Test
        @DisplayName("creates Query, validates no duplicate, saves, and publishes QueryRaisedEvent")
        void raiseQueryCreatesAndSaves() {
            CrfAssessment assessment = mockAssessmentWithFieldLabel(FIELD_LABEL);
            when(assessmentRepository.getById(ASSESSMENT_ID)).thenReturn(assessment);

            RaiseQueryCommand command = new RaiseQueryCommand(
                    ASSESSMENT_ID, FIELD_CODE, null, FIELD_TYPE,
                    "Why is this value so high?",
                    FIELD_CODE, "oldValue", "Old Value", 1L);

            Query result = service.raiseQuery(command);

            assertThat(result.getStatus()).isEqualTo(QueryStatus.OPEN);
            assertThat(result.getType()).isEqualTo(QueryType.MONITOR_QUERY);
            assertThat(result.getQuestion()).isEqualTo("Why is this value so high?");
            assertThat(result.getAssessmentId()).isEqualTo(ASSESSMENT_ID);
            assertThat(result.getFieldIdentifier().getFieldCode()).isEqualTo(FIELD_CODE);
            assertThat(result.getOriginalValue()).isNotNull();
            assertThat(result.getOriginalValue().getFieldLabel()).isEqualTo(FIELD_LABEL);

            verify(duplicateValidationService).validateNoDuplicate(
                    eq(ASSESSMENT_ID), any(QueryFieldIdentifier.class));
            verify(assessmentRepository).getById(ASSESSMENT_ID);
            verify(queryLifecycleService).raiseQuery(assessment);
            verify(queryRepository).save(any(Query.class));
            verify(assessmentRepository).save(assessment);
            verify(eventBus).publishAll(any(Query.class));
        }

        /**
         * 验证raiseQuery()发布的QueryRaisedEvent包含正确的queryId、assessmentId和fieldIdentifier字段。
         * 前置条件：表单评估模拟成功，
         * 执行raiseQuery()后发布的QueryRaisedEvent中各字段与命令参数一致。
         */
        @Test
        @DisplayName("raiseQuery publishes QueryRaisedEvent with correct fields")
        void raiseQueryEventFields() {
            CrfAssessment assessment = mockAssessmentWithFieldLabel(FIELD_LABEL);
            when(assessmentRepository.getById(ASSESSMENT_ID)).thenReturn(assessment);

            RaiseQueryCommand command = new RaiseQueryCommand(
                    ASSESSMENT_ID, FIELD_CODE, null, FIELD_TYPE,
                    "Question", FIELD_CODE, "val", "text", 1L);

            Query result = service.raiseQuery(command);

            List<DomainEvent> events = result.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(QueryRaisedEvent.class);

            QueryRaisedEvent event = (QueryRaisedEvent) events.get(0);
            assertThat(event.getQueryId()).isEqualTo(result.getId());
            assertThat(event.getAssessmentId()).isEqualTo(ASSESSMENT_ID);
            assertThat(event.getFieldIdentifier().getFieldCode()).isEqualTo(FIELD_CODE);
        }

        /**
         * 验证raiseQuery()传入null命令时抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"command"。
         */
        @Test
        @DisplayName("raiseQuery with null command throws NullPointerException")
        void raiseQueryNullCommand() {
            assertThatThrownBy(() -> service.raiseQuery(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("command");
        }

        /**
         * 验证当表单中找不到字段标签时，raiseQuery()回退使用字段编码作为字段标签。
         * 前置条件：表单评估中findFieldValueByCode()返回空，
         * 执行raiseQuery()后originalValue的fieldLabel等于fieldCode。
         */
        @Test
        @DisplayName("raiseQuery when field label not found uses field code as label")
        void raiseQueryFallbackFieldLabel() {
            // Create assessment where findFieldValueByCode returns empty
            CrfAssessment assessment = mock(CrfAssessment.class);
            when(assessment.findFieldValueByCode(anyString()))
                    .thenReturn(Optional.empty());
            when(assessmentRepository.getById(ASSESSMENT_ID)).thenReturn(assessment);

            RaiseQueryCommand command = new RaiseQueryCommand(
                    ASSESSMENT_ID, FIELD_CODE, null, FIELD_TYPE,
                    "Question", FIELD_CODE, "val", "text", 1L);

            Query result = service.raiseQuery(command);

            // Should fall back to fieldCode as fieldLabel
            assertThat(result.getOriginalValue().getFieldLabel()).isEqualTo(FIELD_CODE);
            assertThat(result.getOriginalValue().getFieldCode()).isEqualTo(FIELD_CODE);
        }
    }

    // ===============================================================
    // respondToQuery Tests
    // ===============================================================

    @Nested
    @DisplayName("respondToQuery")
    class RespondToQueryTests {

        /**
         * 验证respondToQuery()以CLARIFY_ONLY（仅澄清）方式回应质疑。
         * 前置条件：质疑处于OPEN(待回应)状态，表单评估模拟成功，
         * 执行respondToQuery()后质疑状态为RESPONDED(已回应)，
         * updateType为CLARIFY_ONLY，currentValue使用原始值（未修改），
         * 触发查询仓库保存和事件发布。
         */
        @Test
        @DisplayName("loads query, responds with CLARIFY_ONLY, saves, and publishes QueryRespondedEvent")
        void respondToQueryClarifyOnly() {
            // Create an OPEN query and set up mocks
            Query openQuery = createOpenQuery();
            when(queryRepository.getById(openQuery.getId())).thenReturn(openQuery);

            CrfAssessment assessment = mockAssessmentWithFieldLabel(FIELD_LABEL);
            when(assessmentRepository.getById(ASSESSMENT_ID)).thenReturn(assessment);

            RespondToQueryCommand command = new RespondToQueryCommand(
                    openQuery.getId(),
                    "Patient had a fever",
                    QueryUpdateType.CLARIFY_ONLY,
                    null, null, 2L);

            Query result = service.respondToQuery(command);

            assertThat(result.getStatus()).isEqualTo(QueryStatus.RESPONDED);
            assertThat(result.getResponse()).isEqualTo("Patient had a fever");
            assertThat(result.getUpdateType()).isEqualTo(QueryUpdateType.CLARIFY_ONLY);
            assertThat(result.getCurrentValue()).isNotNull();
            // For CLARIFY_ONLY, current value should use original value
            assertThat(result.getCurrentValue().getFieldValue())
                    .isEqualTo(openQuery.getOriginalValue().getFieldValue());

            verify(queryRepository).getById(openQuery.getId());
            verify(assessmentRepository).getById(ASSESSMENT_ID);
            verify(queryRepository).save(any(Query.class));
            verify(eventBus).publishAll(any(Query.class));
        }

        /**
         * 验证respondToQuery()以MODIFY_VALUE（修改值）方式回应质疑，更新评估字段值。
         * 前置条件：质疑处于OPEN(待回应)状态，表单评估模拟成功，
         * 执行respondToQuery()后质疑状态为RESPONDED(已回应)，
         * updateType为MODIFY_VALUE，currentValue使用命令中提供的新值，
         * 同时保存质疑和评估，并发布事件。
         */
        @Test
        @DisplayName("respondToQuery with MODIFY_VALUE updates assessment field value and saves both")
        void respondToQueryModifyValue() {
            Query openQuery = createOpenQuery();
            when(queryRepository.getById(openQuery.getId())).thenReturn(openQuery);

            CrfAssessment assessment = mockAssessmentWithFieldLabel(FIELD_LABEL);
            when(assessmentRepository.getById(ASSESSMENT_ID)).thenReturn(assessment);

            RespondToQueryCommand command = new RespondToQueryCommand(
                    openQuery.getId(),
                    "Confirmed data entry error, corrected",
                    QueryUpdateType.MODIFY_VALUE,
                    "correctedValue", "Corrected Value", 2L);

            Query result = service.respondToQuery(command);

            assertThat(result.getStatus()).isEqualTo(QueryStatus.RESPONDED);
            assertThat(result.getUpdateType()).isEqualTo(QueryUpdateType.MODIFY_VALUE);
            // For MODIFY_VALUE, current value should use the new field value from command
            assertThat(result.getCurrentValue().getFieldValue()).isEqualTo("correctedValue");
            assertThat(result.getCurrentValue().getFieldValueText()).isEqualTo("Corrected Value");

            verify(queryRepository).save(any(Query.class));
            verify(assessmentRepository).save(assessment);
            verify(eventBus).publishAll(any(Query.class));
        }

        /**
         * 验证respondToQuery()传入null命令时抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"command"。
         */
        @Test
        @DisplayName("respondToQuery with null command throws NullPointerException")
        void respondToQueryNullCommand() {
            assertThatThrownBy(() -> service.respondToQuery(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("command");
        }

        /**
         * 验证respondToQuery()发布的QueryRespondedEvent包含正确的queryId和assessmentId字段。
         * 前置条件：质疑处于OPEN(待回应)状态，表单评估模拟成功，
         * 执行respondToQuery()后通过eventBus.publishAll()捕获的QueryRespondedEvent
         * 各字段与预期一致。
         */
        @Test
        @DisplayName("respondToQuery publishes QueryRespondedEvent")
        void respondToQueryEventFields() {
            Query openQuery = createOpenQuery();
            when(queryRepository.getById(openQuery.getId())).thenReturn(openQuery);

            CrfAssessment assessment = mockAssessmentWithFieldLabel(FIELD_LABEL);
            when(assessmentRepository.getById(ASSESSMENT_ID)).thenReturn(assessment);

            RespondToQueryCommand command = new RespondToQueryCommand(
                    openQuery.getId(), "Response",
                    QueryUpdateType.CLARIFY_ONLY, null, null, 2L);

            service.respondToQuery(command);

            // Capture the published query and verify its events
            org.mockito.ArgumentCaptor<Query> queryCaptor =
                    org.mockito.ArgumentCaptor.forClass(Query.class);
            verify(eventBus).publishAll(queryCaptor.capture());

            List<DomainEvent> events = queryCaptor.getValue().pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(QueryRespondedEvent.class);

            QueryRespondedEvent event = (QueryRespondedEvent) events.get(0);
            assertThat(event.getQueryId()).isEqualTo(openQuery.getId());
            assertThat(event.getAssessmentId()).isEqualTo(ASSESSMENT_ID);
        }
    }

    // ===============================================================
    // closeQuery Tests
    // ===============================================================

    @Nested
    @DisplayName("closeQuery")
    class CloseQueryTests {

        /**
         * 验证closeQuery()用例加载质疑、执行关闭、保存并发布QueryClosedEvent(质疑关闭事件)。
         * 前置条件：质疑处于OPEN(待回应)状态，表单评估模拟成功，
         * 执行closeQuery()后质疑状态为CLOSED(已关闭)，
         * 触发质疑生命周期服务和评估仓库保存操作。
         */
        @Test
        @DisplayName("loads query, calls close, saves, and publishes QueryClosedEvent")
        void closeQuery() {
            Query openQuery = createOpenQuery();
            when(queryRepository.getById(openQuery.getId())).thenReturn(openQuery);

            CrfAssessment assessment = mock(CrfAssessment.class);
            when(assessmentRepository.getById(ASSESSMENT_ID)).thenReturn(assessment);

            CloseQueryCommand command = new CloseQueryCommand(openQuery.getId(), 1L);

            Query result = service.closeQuery(command);

            assertThat(result.getStatus()).isEqualTo(QueryStatus.CLOSED);

            verify(queryRepository).getById(openQuery.getId());
            verify(assessmentRepository).getById(ASSESSMENT_ID);
            verify(queryLifecycleService).closeQuery(assessment, ASSESSMENT_ID);
            verify(queryRepository).save(any(Query.class));
            verify(assessmentRepository).save(assessment);
            verify(eventBus).publishAll(any(Query.class));
        }

        /**
         * 验证closeQuery()发布的QueryClosedEvent包含正确的queryId和assessmentId字段。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 执行closeQuery()后发布的QueryClosedEvent各字段与预期一致。
         */
        @Test
        @DisplayName("closeQuery publishes QueryClosedEvent")
        void closeQueryEventFields() {
            Query openQuery = createOpenQuery();
            when(queryRepository.getById(openQuery.getId())).thenReturn(openQuery);

            CrfAssessment assessment = mock(CrfAssessment.class);
            when(assessmentRepository.getById(ASSESSMENT_ID)).thenReturn(assessment);

            CloseQueryCommand command = new CloseQueryCommand(openQuery.getId(), 1L);
            Query result = service.closeQuery(command);

            List<DomainEvent> events = result.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(QueryClosedEvent.class);

            QueryClosedEvent event = (QueryClosedEvent) events.get(0);
            assertThat(event.getQueryId()).isEqualTo(openQuery.getId());
            assertThat(event.getAssessmentId()).isEqualTo(ASSESSMENT_ID);
        }

        /**
         * 验证closeQuery()传入null命令时抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"command"。
         */
        @Test
        @DisplayName("closeQuery with null command throws NullPointerException")
        void closeQueryNullCommand() {
            assertThatThrownBy(() -> service.closeQuery(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("command");
        }
    }

    // ===============================================================
    // reopenQuery Tests
    // ===============================================================

    @Nested
    @DisplayName("reopenQuery")
    class ReopenQueryTests {

        /**
         * 验证reopenQuery()用例加载已关闭质疑、执行重开、保存并发布QueryReopenedEvent(质疑重开事件)。
         * 前置条件：质疑处于CLOSED(已关闭)状态，
         * 执行reopenQuery()后质疑状态为OPEN(待回应)，
         * 问题更新为新问题，答复被清空，
         * 触发查询仓库保存和事件发布。
         */
        @Test
        @DisplayName("loads closed query, reopens, saves, and publishes QueryReopenedEvent")
        void reopenQuery() {
            Query closedQuery = createClosedQuery();
            when(queryRepository.getById(closedQuery.getId())).thenReturn(closedQuery);

            ReopenQueryCommand command = new ReopenQueryCommand(
                    closedQuery.getId(), "Need more investigation", 2L);

            Query result = service.reopenQuery(command);

            assertThat(result.getStatus()).isEqualTo(QueryStatus.OPEN);
            assertThat(result.getQuestion()).isEqualTo("Need more investigation");
            assertThat(result.getResponse()).isNull();

            verify(queryRepository).getById(closedQuery.getId());
            verify(queryRepository).save(any(Query.class));
            verify(eventBus).publishAll(any(Query.class));
        }

        /**
         * 验证reopenQuery()发布的QueryReopenedEvent包含正确的queryId、assessmentId和reason字段。
         * 前置条件：质疑处于CLOSED(已关闭)状态，
         * 执行reopenQuery()后发布的QueryReopenedEvent各字段与预期一致。
         */
        @Test
        @DisplayName("reopenQuery publishes QueryReopenedEvent with correct reason")
        void reopenQueryEventFields() {
            Query closedQuery = createClosedQuery();
            when(queryRepository.getById(closedQuery.getId())).thenReturn(closedQuery);

            ReopenQueryCommand command = new ReopenQueryCommand(
                    closedQuery.getId(), "New information available", 2L);
            Query result = service.reopenQuery(command);

            List<DomainEvent> events = result.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(QueryReopenedEvent.class);

            QueryReopenedEvent event = (QueryReopenedEvent) events.get(0);
            assertThat(event.getQueryId()).isEqualTo(closedQuery.getId());
            assertThat(event.getAssessmentId()).isEqualTo(ASSESSMENT_ID);
            assertThat(event.getReason()).isEqualTo("New information available");
        }

        /**
         * 验证reopenQuery()传入null命令时抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"command"。
         */
        @Test
        @DisplayName("reopenQuery with null command throws NullPointerException")
        void reopenQueryNullCommand() {
            assertThatThrownBy(() -> service.reopenQuery(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("command");
        }
    }

    // ---------------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------------

    /**
     * Creates a Query in OPEN state with known ASSESSMENT_ID and FIELD_CODE.
     */
    private Query createOpenQuery() {
        Query q = Query.raise(
                new QueryId(1L),
                ASSESSMENT_ID,
                new QueryFieldIdentifier(FIELD_CODE, null, FIELD_TYPE),
                QueryType.MONITOR_QUERY,
                "Why is this value so high?",
                new SnapshotValue(FIELD_CODE, FIELD_LABEL, "oldValue", "Old Value", new Date()),
                1L
        );
        q.pullDomainEvents(); // clear the raised event
        return q;
    }

    /**
     * Creates a Query in CLOSED state.
     */
    private Query createClosedQuery() {
        Query q = createOpenQuery();
        q.close(1L);
        q.pullDomainEvents(); // clear the closed event
        return q;
    }
}
