package com.clinicaltrial.ddd.common.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AggregateRoot}.
 * <p>
 * Tests the domain event management behavior of the base aggregate root class,
 * including event registration, retrieval, and clearing.
 * </p>
 */
class AggregateRootTest {

    // ---------------------------------------------------------------
    // Helper implementations
    // ---------------------------------------------------------------

    /**
     * A simple ValueObject for aggregate identity.
     */
    private static class TestId implements ValueObject {
        private final Long value;

        TestId(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestId testId = (TestId) o;
            return Objects.equals(value, testId.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    /**
     * A simple DomainEvent implementation for testing.
     */
    private static class TestEvent implements DomainEvent {
        private final String description;
        private final LocalDateTime occurredOn;

        TestEvent(String description) {
            this.description = description;
            this.occurredOn = LocalDateTime.now();
        }

        @Override
        public LocalDateTime occurredOn() {
            return occurredOn;
        }

        @Override
        public String description() {
            return description;
        }
    }

    /**
     * A concrete AggregateRoot subclass for testing.
     * Exposes registerEvent via a public fireEvent method.
     */
    private static class TestAggregate extends AggregateRoot<TestId> {
        private final TestId id;

        TestAggregate(Long id) {
            this.id = new TestId(id);
        }

        @Override
        public TestId getId() {
            return id;
        }

        void fireEvent(DomainEvent event) {
            registerEvent(event);
        }
    }

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------

    /**
     * 验证注册事件后调用 pullDomainEvents() 能正确返回已注册的事件，并清空内部事件列表。
     * 前置条件：创建一个聚合根实例，通过 fireEvent() 注册一个 TestEvent 事件。
     * 预期结果：第一次调用 pullDomainEvents() 返回包含该事件的列表（大小=1，描述包含"event1"）；
     *          第二次调用 pullDomainEvents() 返回空列表，确认事件被消费后自动清除。
     */
    @Test
    @DisplayName("pullDomainEvents returns registered events and clears the internal list")
    void pullDomainEventsReturnsRegisteredEventsAndClears() {
        TestAggregate aggregate = new TestAggregate(1L);
        aggregate.fireEvent(new TestEvent("event1"));

        List<DomainEvent> events = aggregate.pullDomainEvents();

        assertThat(events)
                .hasSize(1)
                .first()
                .matches(e -> e.description().contains("event1"));

        // After pulling, the list should be empty
        assertThat(aggregate.pullDomainEvents()).isEmpty();
    }

    /**
     * 验证未注册任何事件的空聚合根调用 pullDomainEvents() 返回空列表。
     * 前置条件：创建一个聚合根实例，不注册任何事件。
     * 预期结果：pullDomainEvents() 返回空列表，说明没有领域事件待消费。
     */
    @Test
    @DisplayName("pullDomainEvents on empty aggregate returns empty list")
    void pullDomainEventsOnEmptyReturnsEmptyList() {
        TestAggregate aggregate = new TestAggregate(1L);

        List<DomainEvent> events = aggregate.pullDomainEvents();

        assertThat(events).isEmpty();
    }

    /**
     * 验证 clearDomainEvents() 清空所有已注册事件，且不返回这些事件。
     * 前置条件：创建一个聚合根实例，先后注册两个 TestEvent 事件。
     * 预期结果：调用 clearDomainEvents() 后，pullDomainEvents() 返回空列表，
     *          说明所有事件已被清除而未被消费。
     */
    @Test
    @DisplayName("clearDomainEvents clears all events without returning them")
    void clearDomainEventsClearsWithoutReturning() {
        TestAggregate aggregate = new TestAggregate(1L);
        aggregate.fireEvent(new TestEvent("event1"));
        aggregate.fireEvent(new TestEvent("event2"));

        aggregate.clearDomainEvents();

        assertThat(aggregate.pullDomainEvents()).isEmpty();
    }

    /**
     * 验证注册 null 事件时 registerEvent() 不做任何操作且不抛出异常。
     * 前置条件：创建一个聚合根实例，通过 fireEvent(null) 传入 null。
     * 预期结果：不抛出任何异常，pullDomainEvents() 返回空列表，说明 null 事件被安全忽略。
     */
    @Test
    @DisplayName("registerEvent with null does nothing and does not throw")
    void registerEventWithNullDoesNothing() {
        TestAggregate aggregate = new TestAggregate(1L);

        // Should not throw
        aggregate.fireEvent(null);

        assertThat(aggregate.pullDomainEvents()).isEmpty();
    }

    /**
     * 验证多个事件按注册顺序依次返回。
     * 前置条件：创建一个聚合根实例，依次注册三个事件（"first", "second", "third"）。
     * 预期结果：pullDomainEvents() 返回大小为3的列表，通过 DomainEvent::description
     *          提取描述内容，断言顺序为 "first"、"second"、"third"，与注册顺序完全一致。
     */
    @Test
    @DisplayName("multiple events are returned in the order they were registered")
    void multipleEventsReturnedInRegistrationOrder() {
        TestAggregate aggregate = new TestAggregate(1L);
        aggregate.fireEvent(new TestEvent("first"));
        aggregate.fireEvent(new TestEvent("second"));
        aggregate.fireEvent(new TestEvent("third"));

        List<DomainEvent> events = aggregate.pullDomainEvents();

        assertThat(events)
                .hasSize(3)
                .extracting(DomainEvent::description)
                .containsExactly("first", "second", "third");
    }

    /**
     * 验证两次 pullDomainEvents() 调用之间的事件隔离——第一次拉取后注册的新事件不会混入之前的批次。
     * 前置条件：① 创建聚合根，注册第一个事件 "batch1"；② 第一次 pullDomainEvents() 拉取并确认大小为1；
     *          ③ 再注册第二个事件 "batch2"。
     * 预期结果：第二次 pullDomainEvents() 返回仅包含 "batch2" 的列表（大小=1），
     *          说明第一次 pull 已清空队列，后续事件独立累积。
     */
    @Test
    @DisplayName("events list is cleared between two pullDomainEvents calls")
    void pullDomainEventsReturnsOnlyNewEventsAfterClear() {
        TestAggregate aggregate = new TestAggregate(1L);
        aggregate.fireEvent(new TestEvent("batch1"));

        List<DomainEvent> firstBatch = aggregate.pullDomainEvents();
        assertThat(firstBatch).hasSize(1);

        // Register more events after first pull
        aggregate.fireEvent(new TestEvent("batch2"));

        List<DomainEvent> secondBatch = aggregate.pullDomainEvents();
        assertThat(secondBatch)
                .hasSize(1)
                .first()
                .matches(e -> e.description().contains("batch2"));
    }

    /**
     * 验证 AggregateRoot 的 equals() 和 hashCode() 基于身份（Identity）比较，而非对象引用。
     * 前置条件：创建三个聚合根——agg1 和 agg2 具有相同 ID(1L)，agg3 具有不同 ID(2L)。
     * 预期结果：agg1 与 agg2 相等且 hashCode 一致；agg1 与 agg3 不相等；agg1 与 agg3 的 hashCode 不同。
     *          说明聚合根的等价性由业务标识（ID）决定，即同一身份的聚合视为同一实体。
     */
    @Test
    @DisplayName("equals and hashCode work by identity")
    void equalsAndHashCodeByIdentity() {
        TestAggregate agg1 = new TestAggregate(1L);
        TestAggregate agg2 = new TestAggregate(1L);
        TestAggregate agg3 = new TestAggregate(2L);

        assertThat(agg1)
                .isEqualTo(agg2)
                .hasSameHashCodeAs(agg2)
                .isNotEqualTo(agg3);

        assertThat(agg1.hashCode()).isNotEqualTo(agg3.hashCode());
    }
}
