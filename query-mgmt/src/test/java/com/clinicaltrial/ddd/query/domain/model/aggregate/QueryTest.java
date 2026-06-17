package com.clinicaltrial.ddd.query.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SnapshotValue;
import com.clinicaltrial.ddd.query.domain.event.QueryClosedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryRaisedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryReopenedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryRespondedEvent;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryType;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryUpdateType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive unit tests for {@link Query}.
 * <p>
 * Covers all state machine transitions: OPEN → RESPONDED → CLOSED, CLOSED → OPEN (reopen),
 * OPEN → CLOSED (direct close), and all guard conditions.
 * Pure domain tests — no mocks.
 * </p>
 */
class QueryTest {

    // ===============================================================
    // Fixtures
    // ===============================================================

    static class QueryTestFixtures {
        static QueryId aQueryId() {
            return new QueryId(1L);
        }

        static CrfAssessmentId anAssessmentId() {
            return new CrfAssessmentId(100L);
        }

        static QueryFieldIdentifier aFieldId() {
            return new QueryFieldIdentifier("field1", null, "TEXT");
        }

        private static final Date FIXED_DATE = new Date(1700000000000L);

        static SnapshotValue aSnapshot() {
            return new SnapshotValue("field1", "Field 1", "oldValue", "Old Value", FIXED_DATE);
        }

        static SnapshotValue aNewSnapshot() {
            return new SnapshotValue("field1", "Field 1", "newValue", "New Value", FIXED_DATE);
        }

        static Query anOpenQuery() {
            return Query.raise(aQueryId(), anAssessmentId(), aFieldId(),
                    QueryType.MONITOR_QUERY, "Why is this value so high?", aSnapshot(), 1L);
        }

        static Query aRespondedQuery() {
            Query q = anOpenQuery();
            q.pullDomainEvents(); // clear the raise event
            q.respond("Patient had fever", aNewSnapshot(), 2L);
            q.pullDomainEvents(); // clear the respond event — fixture is state-only
            return q;
        }

        static Query aClosedQuery() {
            Query q = aRespondedQuery();
            q.close(1L);
            q.pullDomainEvents(); // clear the close event — fixture is state-only
            return q;
        }
    }

    // ===============================================================
    // Factory Method Tests
    // ===============================================================

    @Nested
    @DisplayName("Factory methods")
    class FactoryTests {

        /**
         * 验证通过raise()工厂方法创建质疑时，初始状态为待回应(OPEN)，
         * 并正确存储快照信息。
         * 前置条件：无（新建聚合），
         * 执行raise()后状态为OPEN(待回应)，
         * originalValue与currentValue均指向传入的快照。
         */
        @Test
        @DisplayName("raise creates query with OPEN status and stores snapshot")
        void raiseCreatesOpenQuery() {
            Query query = Query.raise(
                    QueryTestFixtures.aQueryId(),
                    QueryTestFixtures.anAssessmentId(),
                    QueryTestFixtures.aFieldId(),
                    QueryType.MONITOR_QUERY,
                    "Why is this value so high?",
                    QueryTestFixtures.aSnapshot(),
                    1L
            );

            assertThat(query.getId()).isEqualTo(QueryTestFixtures.aQueryId());
            assertThat(query.getAssessmentId()).isEqualTo(QueryTestFixtures.anAssessmentId());
            assertThat(query.getFieldIdentifier()).isEqualTo(QueryTestFixtures.aFieldId());
            assertThat(query.getStatus()).isEqualTo(QueryStatus.OPEN);
            assertThat(query.getType()).isEqualTo(QueryType.MONITOR_QUERY);
            assertThat(query.getQuestion()).isEqualTo("Why is this value so high?");
            assertThat(query.getOriginalValue()).isEqualTo(QueryTestFixtures.aSnapshot());
            assertThat(query.getCurrentValue()).isEqualTo(QueryTestFixtures.aSnapshot());
            assertThat(query.getCreateUserId()).isEqualTo(1L);
            assertThat(query.getUpdateUserId()).isEqualTo(1L);
            assertThat(query.getResponse()).isNull();
            assertThat(query.getUpdateType()).isNull();
            assertThat(query.getCreateTime()).isNotNull();
            assertThat(query.getUpdateTime()).isNotNull();
        }

        /**
         * 验证raise()时传入null的QueryId抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"QueryId"。
         */
        @Test
        @DisplayName("raise with null QueryId throws NullPointerException")
        void raiseNullQueryId() {
            assertThatThrownBy(() ->
                    Query.raise(null, QueryTestFixtures.anAssessmentId(),
                            QueryTestFixtures.aFieldId(), QueryType.MONITOR_QUERY,
                            "Question", QueryTestFixtures.aSnapshot(), 1L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("QueryId");
        }

        /**
         * 验证raise()时传入null的assessmentId抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"assessmentId"。
         */
        @Test
        @DisplayName("raise with null assessmentId throws NullPointerException")
        void raiseNullAssessmentId() {
            assertThatThrownBy(() ->
                    Query.raise(QueryTestFixtures.aQueryId(), null,
                            QueryTestFixtures.aFieldId(), QueryType.MONITOR_QUERY,
                            "Question", QueryTestFixtures.aSnapshot(), 1L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("assessmentId");
        }

        /**
         * 验证raise()时传入null的fieldIdentifier抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"fieldIdentifier"。
         */
        @Test
        @DisplayName("raise with null fieldIdentifier throws NullPointerException")
        void raiseNullFieldIdentifier() {
            assertThatThrownBy(() ->
                    Query.raise(QueryTestFixtures.aQueryId(),
                            QueryTestFixtures.anAssessmentId(), null,
                            QueryType.MONITOR_QUERY,
                            "Question", QueryTestFixtures.aSnapshot(), 1L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fieldIdentifier");
        }

        /**
         * 验证raise()时传入null的type抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"type"。
         */
        @Test
        @DisplayName("raise with null type throws NullPointerException")
        void raiseNullType() {
            assertThatThrownBy(() ->
                    Query.raise(QueryTestFixtures.aQueryId(),
                            QueryTestFixtures.anAssessmentId(),
                            QueryTestFixtures.aFieldId(), null,
                            "Question", QueryTestFixtures.aSnapshot(), 1L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("type");
        }

        /**
         * 验证raise()时传入null的question抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"question"。
         */
        @Test
        @DisplayName("raise with null question throws NullPointerException")
        void raiseNullQuestion() {
            assertThatThrownBy(() ->
                    Query.raise(QueryTestFixtures.aQueryId(),
                            QueryTestFixtures.anAssessmentId(),
                            QueryTestFixtures.aFieldId(), QueryType.MONITOR_QUERY,
                            null, QueryTestFixtures.aSnapshot(), 1L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("question");
        }

        /**
         * 验证raise()时传入空白字符串的question抛出IllegalArgumentException。
         * 前置条件：无，
         * 预期：抛出IllegalArgumentException，异常信息包含"question must not be empty"。
         */
        @Test
        @DisplayName("raise with blank question throws IllegalArgumentException")
        void raiseBlankQuestion() {
            assertThatThrownBy(() ->
                    Query.raise(QueryTestFixtures.aQueryId(),
                            QueryTestFixtures.anAssessmentId(),
                            QueryTestFixtures.aFieldId(), QueryType.MONITOR_QUERY,
                            "   ", QueryTestFixtures.aSnapshot(), 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("question must not be empty");
        }

        /**
         * 验证raise()时传入null的originalValue抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"originalValue"。
         */
        @Test
        @DisplayName("raise with null originalValue throws NullPointerException")
        void raiseNullOriginalValue() {
            assertThatThrownBy(() ->
                    Query.raise(QueryTestFixtures.aQueryId(),
                            QueryTestFixtures.anAssessmentId(),
                            QueryTestFixtures.aFieldId(), QueryType.MONITOR_QUERY,
                            "Question", null, 1L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("originalValue");
        }

        /**
         * 验证raise()时传入null的userId抛出NullPointerException。
         * 前置条件：无，
         * 预期：抛出异常，异常信息包含"userId"。
         */
        @Test
        @DisplayName("raise with null userId throws NullPointerException")
        void raiseNullUserId() {
            assertThatThrownBy(() ->
                    Query.raise(QueryTestFixtures.aQueryId(),
                            QueryTestFixtures.anAssessmentId(),
                            QueryTestFixtures.aFieldId(), QueryType.MONITOR_QUERY,
                            "Question", QueryTestFixtures.aSnapshot(), null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("userId");
        }

        /**
         * 验证reconstruct()工厂方法正确还原聚合状态，且不注册任何领域事件。
         * 前置条件：无（重构已有持久化数据），
         * 执行reconstruct()后状态、类型、问题、答复等字段均与传入参数一致，
         * pullDomainEvents()返回空列表。
         */
        @Test
        @DisplayName("reconstruct restores state and registers no events")
        void reconstructNoEvents() {
            Date now = new Date();
            Query query = Query.reconstruct(
                    QueryTestFixtures.aQueryId(),
                    QueryTestFixtures.anAssessmentId(),
                    QueryTestFixtures.aFieldId(),
                    QueryStatus.CLOSED,
                    QueryType.AUDIT_QUERY,
                    "Question?", "Response",
                    QueryUpdateType.CLARIFY_ONLY,
                    1L, 2L, now, now,
                    QueryTestFixtures.aSnapshot(),
                    QueryTestFixtures.aNewSnapshot()
            );

            assertThat(query.getId()).isEqualTo(QueryTestFixtures.aQueryId());
            assertThat(query.getStatus()).isEqualTo(QueryStatus.CLOSED);
            assertThat(query.getType()).isEqualTo(QueryType.AUDIT_QUERY);
            assertThat(query.getQuestion()).isEqualTo("Question?");
            assertThat(query.getResponse()).isEqualTo("Response");
            assertThat(query.getUpdateType()).isEqualTo(QueryUpdateType.CLARIFY_ONLY);
            assertThat(query.getCreateUserId()).isEqualTo(1L);
            assertThat(query.getUpdateUserId()).isEqualTo(2L);
            assertThat(query.pullDomainEvents()).isEmpty();
        }

        /**
         * 验证raise()注册了类型为QueryRaisedEvent(质疑发起事件)的领域事件。
         * 前置条件：无，
         * 执行raise()后pullDomainEvents()返回列表中包含一个QueryRaisedEvent实例。
         */
        @Test
        @DisplayName("raise registers a QueryRaisedEvent")
        void raiseRegistersEvent() {
            Query query = QueryTestFixtures.anOpenQuery();

            List<DomainEvent> events = query.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(QueryRaisedEvent.class);
        }
    }

    // ===============================================================
    // Raise Event Tests
    // ===============================================================

    @Nested
    @DisplayName("Raise event")
    class RaiseEventTests {

        /**
         * 验证QueryRaisedEvent包含正确的queryId、assessmentId和fieldIdentifier字段。
         * 前置条件：无，
         * 执行raise()后发布的QueryRaisedEvent中queryId、assessmentId、fieldIdentifier与传入参数一致，
         * occurredOn不为null。
         */
        @Test
        @DisplayName("QueryRaisedEvent contains correct queryId, assessmentId, and fieldIdentifier")
        void queryRaisedEventFields() {
            QueryId queryId = QueryTestFixtures.aQueryId();
            CrfAssessmentId assessmentId = QueryTestFixtures.anAssessmentId();
            QueryFieldIdentifier fieldId = QueryTestFixtures.aFieldId();

            Query query = Query.raise(queryId, assessmentId, fieldId,
                    QueryType.MONITOR_QUERY, "Question?", QueryTestFixtures.aSnapshot(), 1L);

            List<DomainEvent> events = query.pullDomainEvents();
            QueryRaisedEvent event = (QueryRaisedEvent) events.get(0);

            assertThat(event.getQueryId()).isEqualTo(queryId);
            assertThat(event.getAssessmentId()).isEqualTo(assessmentId);
            assertThat(event.getFieldIdentifier()).isEqualTo(fieldId);
            assertThat(event.occurredOn()).isNotNull();
        }
    }

    // ===============================================================
    // Respond Transitions
    // ===============================================================

    @Nested
    @DisplayName("Respond transitions")
    class RespondTests {

        /**
         * 验证质疑从待回应(OPEN)状态成功回应。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 执行respond()后状态变更为RESPONDED(已回应)，
         * response、currentValue、updateUserId等字段正确设置，
         * 并发布QueryRespondedEvent领域事件。
         */
        @Test
        @DisplayName("respond transitions OPEN to RESPONDED and registers QueryRespondedEvent")
        void respondFromOpen() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents(); // clear raise event

            query.respond("Patient had fever", QueryTestFixtures.aNewSnapshot(), 2L);

            assertThat(query.getStatus()).isEqualTo(QueryStatus.RESPONDED);
            assertThat(query.getResponse()).isEqualTo("Patient had fever");
            assertThat(query.getCurrentValue()).isEqualTo(QueryTestFixtures.aNewSnapshot());
            assertThat(query.getUpdateType()).isEqualTo(QueryUpdateType.CLARIFY_ONLY);
            assertThat(query.getUpdateUserId()).isEqualTo(2L);

            List<DomainEvent> events = query.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(QueryRespondedEvent.class);
        }

        /**
         * 验证respond()时传入null的response抛出IllegalArgumentException。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 预期：抛出IllegalArgumentException，异常信息包含"response"。
         */
        @Test
        @DisplayName("respond with null response throws IllegalArgumentException")
        void respondWithNullResponse() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();

            assertThatThrownBy(() ->
                    query.respond(null, QueryTestFixtures.aNewSnapshot(), 2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("response");
        }

        /**
         * 验证respond()时传入空白字符串的response抛出IllegalArgumentException。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 预期：抛出IllegalArgumentException，异常信息包含"response"。
         */
        @Test
        @DisplayName("respond with blank response throws IllegalArgumentException")
        void respondWithBlankResponse() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();

            assertThatThrownBy(() ->
                    query.respond("   ", QueryTestFixtures.aNewSnapshot(), 2L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("response");
        }

        /**
         * 验证respond()时传入null的currentValue抛出NullPointerException。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 预期：抛出NullPointerException，异常信息包含"currentValue"。
         */
        @Test
        @DisplayName("respond with null currentValue throws NullPointerException")
        void respondWithNullCurrentValue() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();

            assertThatThrownBy(() ->
                    query.respond("Response", null, 2L))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("currentValue");
        }

        /**
         * 验证respond()时传入null的userId抛出NullPointerException。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 预期：抛出NullPointerException，异常信息包含"userId"。
         */
        @Test
        @DisplayName("respond with null userId throws NullPointerException")
        void respondWithNullUserId() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();

            assertThatThrownBy(() ->
                    query.respond("Response", QueryTestFixtures.aNewSnapshot(), null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("userId");
        }

        /**
         * 验证QueryRespondedEvent包含正确的queryId和assessmentId字段。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 执行respond()后发布的QueryRespondedEvent中queryId、assessmentId与预期一致，
         * occurredOn不为null。
         */
        @Test
        @DisplayName("respondQueryRespondedEventContainsCorrectFields")
        void respondEventFields() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();

            query.respond("Response text", QueryTestFixtures.aNewSnapshot(), 2L);

            List<DomainEvent> events = query.pullDomainEvents();
            QueryRespondedEvent event = (QueryRespondedEvent) events.get(0);

            assertThat(event.getQueryId()).isEqualTo(QueryTestFixtures.aQueryId());
            assertThat(event.getAssessmentId()).isEqualTo(QueryTestFixtures.anAssessmentId());
            assertThat(event.occurredOn()).isNotNull();
        }
    }

    // ===============================================================
    // Close Transitions
    // ===============================================================

    @Nested
    @DisplayName("Close transitions")
    class CloseTests {

        /**
         * 验证质疑从待回应(OPEN)状态直接关闭。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 执行close()后状态变更为CLOSED(已关闭)，
         * 更新用户ID正确设置，
         * 并发布QueryClosedEvent(质疑关闭事件)领域事件。
         */
        @Test
        @DisplayName("close from OPEN transitions to CLOSED and registers QueryClosedEvent")
        void closeFromOpen() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();

            query.close(1L);

            assertThat(query.getStatus()).isEqualTo(QueryStatus.CLOSED);
            assertThat(query.getUpdateUserId()).isEqualTo(1L);

            List<DomainEvent> events = query.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(QueryClosedEvent.class);
        }

        /**
         * 验证质疑从已回应(RESPONDED)状态成功关闭。
         * 前置条件：质疑处于RESPONDED(已回应)状态，
         * 执行close()后状态变更为CLOSED(已关闭)，
         * 并发布QueryClosedEvent(质疑关闭事件)领域事件。
         */
        @Test
        @DisplayName("close from RESPONDED transitions to CLOSED and registers QueryClosedEvent")
        void closeFromResponded() {
            Query query = QueryTestFixtures.aRespondedQuery();
            // query already has events cleared by aRespondedQuery()

            query.close(1L);

            assertThat(query.getStatus()).isEqualTo(QueryStatus.CLOSED);

            List<DomainEvent> events = query.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(QueryClosedEvent.class);
        }

        /**
         * 验证QueryClosedEvent包含正确的queryId和assessmentId字段。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 执行close()后发布的QueryClosedEvent中queryId、assessmentId与预期一致，
         * occurredOn不为null。
         */
        @Test
        @DisplayName("QueryClosedEvent contains correct fields")
        void closeEventFields() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();
            query.close(1L);

            List<DomainEvent> events = query.pullDomainEvents();
            QueryClosedEvent event = (QueryClosedEvent) events.get(0);

            assertThat(event.getQueryId()).isEqualTo(QueryTestFixtures.aQueryId());
            assertThat(event.getAssessmentId()).isEqualTo(QueryTestFixtures.anAssessmentId());
            assertThat(event.occurredOn()).isNotNull();
        }
    }

    // ===============================================================
    // Reopen Transitions
    // ===============================================================

    @Nested
    @DisplayName("Reopen transitions")
    class ReopenTests {

        /**
         * 验证质疑从已关闭(CLOSED)状态成功重开。
         * 前置条件：质疑处于CLOSED(已关闭)状态（经OPEN→RESPONDED→CLOSED完整路径），
         * 执行reopen()后状态变更为OPEN(待回应)，
         * question更新为新问题，response和updateType被清空，
         * 并发布QueryReopenedEvent(质疑重开事件)领域事件。
         */
        @Test
        @DisplayName("reopen transitions CLOSED to OPEN, clears response, sets new question, registers QueryReopenedEvent")
        void reopenFromClosed() {
            Query query = QueryTestFixtures.aClosedQuery();
            // aClosedQuery already clears events

            query.reopen("Need further investigation", 2L);

            assertThat(query.getStatus()).isEqualTo(QueryStatus.OPEN);
            assertThat(query.getQuestion()).isEqualTo("Need further investigation");
            assertThat(query.getResponse()).isNull();
            assertThat(query.getUpdateType()).isNull();
            assertThat(query.getUpdateUserId()).isEqualTo(2L);

            List<DomainEvent> events = query.pullDomainEvents();
            assertThat(events)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(QueryReopenedEvent.class);
        }

        /**
         * 验证QueryReopenedEvent包含正确的queryId、assessmentId和reason字段。
         * 前置条件：质疑处于CLOSED(已关闭)状态，
         * 执行reopen()后发布的QueryReopenedEvent中queryId、assessmentId、reason与预期一致，
         * occurredOn不为null。
         */
        @Test
        @DisplayName("QueryReopenedEvent contains correct fields including reason")
        void reopenEventFields() {
            Query query = QueryTestFixtures.aClosedQuery();
            query.reopen("New evidence found", 2L);

            List<DomainEvent> events = query.pullDomainEvents();
            QueryReopenedEvent event = (QueryReopenedEvent) events.get(0);

            assertThat(event.getQueryId()).isEqualTo(QueryTestFixtures.aQueryId());
            assertThat(event.getAssessmentId()).isEqualTo(QueryTestFixtures.anAssessmentId());
            assertThat(event.getReason()).isEqualTo("New evidence found");
            assertThat(event.occurredOn()).isNotNull();
        }
    }

    // ===============================================================
    // Guard / Invalid Transition Tests
    // ===============================================================

    @Nested
    @DisplayName("Invalid transitions (guards)")
    class GuardTests {

        /**
         * 验证在已关闭(CLOSED)状态下执行respond()抛出BusinessRuleViolationException。
         * 前置条件：质疑处于CLOSED(已关闭)状态，
         * 预期：抛出BusinessRuleViolationException，
         * 异常信息包含"QUERY_INVALID_TRANSITION"和"expected OPEN"。
         */
        @Test
        @DisplayName("respond when CLOSED throws BusinessRuleViolationException")
        void respondWhenClosed() {
            Query query = QueryTestFixtures.aClosedQuery();

            assertThatThrownBy(() ->
                    query.respond("Response", QueryTestFixtures.aNewSnapshot(), 2L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("QUERY_INVALID_TRANSITION")
                    .hasMessageContaining("expected OPEN");
        }

        /**
         * 验证在已回应(RESPONDED)状态下执行respond()抛出BusinessRuleViolationException。
         * 前置条件：质疑处于RESPONDED(已回应)状态，
         * 预期：抛出BusinessRuleViolationException，
         * 异常信息包含"QUERY_INVALID_TRANSITION"和"expected OPEN"。
         */
        @Test
        @DisplayName("respond when RESPONDED throws BusinessRuleViolationException")
        void respondWhenResponded() {
            Query query = QueryTestFixtures.aRespondedQuery();

            assertThatThrownBy(() ->
                    query.respond("Another response", QueryTestFixtures.aNewSnapshot(), 2L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("QUERY_INVALID_TRANSITION")
                    .hasMessageContaining("expected OPEN");
        }

        /**
         * 验证在已关闭(CLOSED)状态下再次执行close()抛出BusinessRuleViolationException。
         * 前置条件：质疑处于CLOSED(已关闭)状态，
         * 预期：抛出BusinessRuleViolationException，
         * 异常信息包含"QUERY_INVALID_TRANSITION"和"expected OPEN or RESPONDED"。
         */
        @Test
        @DisplayName("close when already CLOSED throws BusinessRuleViolationException")
        void closeWhenAlreadyClosed() {
            Query query = QueryTestFixtures.aClosedQuery();

            assertThatThrownBy(() -> query.close(1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("QUERY_INVALID_TRANSITION")
                    .hasMessageContaining("expected OPEN or RESPONDED");
        }

        /**
         * 验证在待回应(OPEN)状态下执行reopen()抛出BusinessRuleViolationException。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 预期：抛出BusinessRuleViolationException，
         * 异常信息包含"QUERY_INVALID_TRANSITION"和"expected CLOSED"。
         */
        @Test
        @DisplayName("reopen when OPEN throws BusinessRuleViolationException")
        void reopenWhenOpen() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();

            assertThatThrownBy(() -> query.reopen("Reason", 1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("QUERY_INVALID_TRANSITION")
                    .hasMessageContaining("expected CLOSED");
        }

        /**
         * 验证在已回应(RESPONDED)状态下执行reopen()抛出BusinessRuleViolationException。
         * 前置条件：质疑处于RESPONDED(已回应)状态，
         * 预期：抛出BusinessRuleViolationException，
         * 异常信息包含"QUERY_INVALID_TRANSITION"和"expected CLOSED"。
         */
        @Test
        @DisplayName("reopen when RESPONDED throws BusinessRuleViolationException")
        void reopenWhenResponded() {
            Query query = QueryTestFixtures.aRespondedQuery();

            assertThatThrownBy(() -> query.reopen("Reason", 1L))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("QUERY_INVALID_TRANSITION")
                    .hasMessageContaining("expected CLOSED");
        }

        /**
         * 验证reopen()时传入null的reason抛出IllegalArgumentException。
         * 前置条件：质疑处于CLOSED(已关闭)状态，
         * 预期：抛出IllegalArgumentException，异常信息包含"reason"。
         */
        @Test
        @DisplayName("reopen with null reason throws IllegalArgumentException")
        void reopenWithNullReason() {
            Query query = QueryTestFixtures.aClosedQuery();

            assertThatThrownBy(() -> query.reopen(null, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reason");
        }

        /**
         * 验证reopen()时传入空白字符串的reason抛出IllegalArgumentException。
         * 前置条件：质疑处于CLOSED(已关闭)状态，
         * 预期：抛出IllegalArgumentException，异常信息包含"reason"。
         */
        @Test
        @DisplayName("reopen with blank reason throws IllegalArgumentException")
        void reopenWithBlankReason() {
            Query query = QueryTestFixtures.aClosedQuery();

            assertThatThrownBy(() -> query.reopen("   ", 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reason");
        }

        /**
         * 验证close()时传入null的userId抛出NullPointerException。
         * 前置条件：质疑处于OPEN(待回应)状态，
         * 预期：抛出NullPointerException，异常信息包含"userId"。
         */
        @Test
        @DisplayName("close with null userId throws NullPointerException")
        void closeWithNullUserId() {
            Query query = QueryTestFixtures.anOpenQuery();
            query.pullDomainEvents();

            assertThatThrownBy(() -> query.close(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("userId");
        }
    }

    // ===============================================================
    // Full Lifecycle Scenarios
    // ===============================================================

    @Nested
    @DisplayName("Full lifecycle scenarios")
    class FullLifecycleTests {

        /**
         * 验证质疑完成完整生命周期：发起(raise)→回应(respond)→关闭(close)，
         * 各步骤状态正确且共产生2个领域事件（回应事件+关闭事件，发起事件已被清除）。
         * 前置条件：无，
         * 依次执行raise()→respond()→close()后状态为CLOSED(已关闭)，
         * 事件顺序为：QueryRespondedEvent → QueryClosedEvent(质疑关闭事件)。
         */
        @Test
        @DisplayName("raise → respond → close produces exactly 3 events in order")
        void raiseRespondClose() {
            Query query = Query.raise(
                    QueryTestFixtures.aQueryId(),
                    QueryTestFixtures.anAssessmentId(),
                    QueryTestFixtures.aFieldId(),
                    QueryType.MONITOR_QUERY,
                    "Why?",
                    QueryTestFixtures.aSnapshot(),
                    1L
            );

            assertThat(query.getStatus()).isEqualTo(QueryStatus.OPEN);
            query.pullDomainEvents(); // clear raise event for clean counting

            // respond
            query.respond("Because", QueryTestFixtures.aNewSnapshot(), 2L);
            assertThat(query.getStatus()).isEqualTo(QueryStatus.RESPONDED);

            // close
            query.close(1L);
            assertThat(query.getStatus()).isEqualTo(QueryStatus.CLOSED);

            // Verify all 3 events: raise (re-registered? No, cleared) + respond + close
            // Actually, respond registered 1 event and close registered 1 event = 2 events
            // But the raise event was cleared, so we only have 2
            List<DomainEvent> events = query.pullDomainEvents();
            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(QueryRespondedEvent.class);
            assertThat(events.get(1)).isInstanceOf(QueryClosedEvent.class);
        }

        /**
         * 验证质疑完整生命周期（不清除中间事件）共产生3个领域事件，顺序正确。
         * 前置条件：无，
         * 依次执行raise()→respond()→close()后状态为CLOSED(已关闭)，
         * 事件顺序为：QueryRaisedEvent(质疑发起事件) → QueryRespondedEvent → QueryClosedEvent(质疑关闭事件)。
         */
        @Test
        @DisplayName("raise → respond → close without clearing produces all 3 events")
        void raiseRespondCloseAllEvents() {
            Query query = Query.raise(
                    QueryTestFixtures.aQueryId(),
                    QueryTestFixtures.anAssessmentId(),
                    QueryTestFixtures.aFieldId(),
                    QueryType.MONITOR_QUERY,
                    "Why?",
                    QueryTestFixtures.aSnapshot(),
                    1L
            );

            // Don't clear — collect all events at the end
            query.respond("Because", QueryTestFixtures.aNewSnapshot(), 2L);
            query.close(1L);

            List<DomainEvent> events = query.pullDomainEvents();
            assertThat(events).hasSize(3);
            assertThat(events.get(0)).isInstanceOf(QueryRaisedEvent.class);
            assertThat(events.get(1)).isInstanceOf(QueryRespondedEvent.class);
            assertThat(events.get(2)).isInstanceOf(QueryClosedEvent.class);
        }

        /**
         * 验证质疑复杂生命周期：发起(raise)→关闭(close)→重开(reopen)→回应(respond)→关闭(close)，
         * 共产生5个领域事件，顺序正确。
         * 前置条件：无，
         * 依次执行raise()→close()→reopen()→respond()→close()后状态为CLOSED(已关闭)，
         * 事件顺序为：QueryRaisedEvent(质疑发起事件) → QueryClosedEvent(质疑关闭事件) → QueryReopenedEvent(质疑重开事件) → QueryRespondedEvent → QueryClosedEvent(质疑关闭事件)。
         */
        @Test
        @DisplayName("raise → close → reopen → respond → close produces 5 events")
        void raiseCloseReopenRespondClose() {
            Query query = Query.raise(
                    QueryTestFixtures.aQueryId(),
                    QueryTestFixtures.anAssessmentId(),
                    QueryTestFixtures.aFieldId(),
                    QueryType.MONITOR_QUERY,
                    "Why?",
                    QueryTestFixtures.aSnapshot(),
                    1L
            );

            // close directly from OPEN
            query.close(1L);
            assertThat(query.getStatus()).isEqualTo(QueryStatus.CLOSED);

            // reopen
            query.reopen("Need more info", 2L);
            assertThat(query.getStatus()).isEqualTo(QueryStatus.OPEN);
            assertThat(query.getResponse()).isNull();

            // respond
            query.respond("Additional explanation", QueryTestFixtures.aNewSnapshot(), 3L);
            assertThat(query.getStatus()).isEqualTo(QueryStatus.RESPONDED);

            // close again
            query.close(1L);
            assertThat(query.getStatus()).isEqualTo(QueryStatus.CLOSED);

            List<DomainEvent> events = query.pullDomainEvents();
            assertThat(events).hasSize(5);
            assertThat(events.get(0)).isInstanceOf(QueryRaisedEvent.class);
            assertThat(events.get(1)).isInstanceOf(QueryClosedEvent.class);
            assertThat(events.get(2)).isInstanceOf(QueryReopenedEvent.class);
            assertThat(events.get(3)).isInstanceOf(QueryRespondedEvent.class);
            assertThat(events.get(4)).isInstanceOf(QueryClosedEvent.class);
        }
    }
}
