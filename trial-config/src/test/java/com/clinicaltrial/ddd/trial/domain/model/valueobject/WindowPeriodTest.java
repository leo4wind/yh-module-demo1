package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link WindowPeriod}.
 * <p>
 * Tests the window boundary checking logic as well as validation constraints.
 * </p>
 */
class WindowPeriodTest {

    private static final long DAY_MS = TimeUnit.DAYS.toMillis(1);

    private Date targetDate;
    private WindowPeriod window;

    @BeforeEach
    void setUp() {
        // Target date: 2026-06-16 12:00:00 UTC
        targetDate = new Date(1765900800000L);  // June 16, 2026 00:00:00 UTC
        window = new WindowPeriod(3L, 7L);
    }

    // ---------------------------------------------------------------
    // contains() boundary tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("contains() boundary checks")
    class ContainsTests {

        /**
         * 验证当待检查日期与目标日期完全相同时，contains() 返回 true。
         * 前置条件：WindowPeriod(beforeDays=3, afterDays=7)，目标日期 targetDate 与待检查日期相同。
         * 预期结果：contains() 返回 true，说明目标日期本身始终在窗口范围内。
         */
        @Test
        @DisplayName("date exactly at target date returns true")
        void exactlyAtTarget() {
            assertThat(window.contains(targetDate, targetDate)).isTrue();
        }

        /**
         * 验证当待检查日期在目标日期之前且在窗口范围内时，contains() 返回 true。
         * 前置条件：WindowPeriod(beforeDays=3, afterDays=7)，待检查日期比目标日期早2天（在 beforeDays=3 范围内）。
         * 预期结果：contains() 返回 true，说明日期落在前向窗口边界内。
         */
        @Test
        @DisplayName("date within window (before the target) returns true")
        void dateWithinWindowBeforeTarget() {
            Date twoDaysBefore = new Date(targetDate.getTime() - 2 * DAY_MS);
            assertThat(window.contains(targetDate, twoDaysBefore)).isTrue();
        }

        /**
         * 验证当待检查日期在目标日期之后且在窗口范围内时，contains() 返回 true。
         * 前置条件：WindowPeriod(beforeDays=3, afterDays=7)，待检查日期比目标日期晚5天（在 afterDays=7 范围内）。
         * 预期结果：contains() 返回 true，说明日期落在后向窗口边界内。
         */
        @Test
        @DisplayName("date within window (after the target) returns true")
        void dateWithinWindowAfterTarget() {
            Date fiveDaysAfter = new Date(targetDate.getTime() + 5 * DAY_MS);
            assertThat(window.contains(targetDate, fiveDaysAfter)).isTrue();
        }

        /**
         * 验证当待检查日期恰好等于前向边界（目标日期前 beforeDays 天）时，contains() 返回 true。
         * 前置条件：WindowPeriod(beforeDays=3, afterDays=7)，待检查日期比目标日期早3天（精确边界）。
         * 预期结果：contains() 返回 true，说明窗口边界是闭区间（含端点）。
         */
        @Test
        @DisplayName("date exactly at before boundary returns true")
        void exactlyAtBeforeBoundary() {
            Date exactlyBeforeBoundary = new Date(targetDate.getTime() - 3 * DAY_MS);
            assertThat(window.contains(targetDate, exactlyBeforeBoundary)).isTrue();
        }

        /**
         * 验证当待检查日期恰好等于后向边界（目标日期后 afterDays 天）时，contains() 返回 true。
         * 前置条件：WindowPeriod(beforeDays=3, afterDays=7)，待检查日期比目标日期晚7天（精确边界）。
         * 预期结果：contains() 返回 true，说明窗口边界是闭区间（含端点）。
         */
        @Test
        @DisplayName("date exactly at after boundary returns true")
        void exactlyAtAfterBoundary() {
            Date exactlyAfterBoundary = new Date(targetDate.getTime() + 7 * DAY_MS);
            assertThat(window.contains(targetDate, exactlyAfterBoundary)).isTrue();
        }

        /**
         * 验证当待检查日期超出前向窗口边界时，contains() 返回 false。
         * 前置条件：WindowPeriod(beforeDays=3, afterDays=7)，待检查日期比目标日期早4天（超出 beforeDays=3 边界）。
         * 预期结果：contains() 返回 false，说明超出前向窗口的日期被正确排除。
         */
        @Test
        @DisplayName("date before window returns false")
        void dateBeforeWindowReturnsFalse() {
            Date fourDaysBefore = new Date(targetDate.getTime() - 4 * DAY_MS);
            assertThat(window.contains(targetDate, fourDaysBefore)).isFalse();
        }

        /**
         * 验证当待检查日期超出后向窗口边界时，contains() 返回 false。
         * 前置条件：WindowPeriod(beforeDays=3, afterDays=7)，待检查日期比目标日期晚8天（超出 afterDays=7 边界）。
         * 预期结果：contains() 返回 false，说明超出后向窗口的日期被正确排除。
         */
        @Test
        @DisplayName("date after window returns false")
        void dateAfterWindowReturnsFalse() {
            Date eightDaysAfter = new Date(targetDate.getTime() + 8 * DAY_MS);
            assertThat(window.contains(targetDate, eightDaysAfter)).isFalse();
        }

        /**
         * 验证零天窗口（beforeDays=0, afterDays=0）的边界行为：仅匹配目标日期本身。
         * 前置条件：WindowPeriod(0L, 0L)，分别检查 targetDate 本身、前一天和后一天。
         * 预期结果：目标日期返回 true，前一天和后一天均返回 false，说明零宽度窗口仅接受精确匹配。
         */
        @Test
        @DisplayName("zero-day window only matches exact date")
        void zeroDayWindowOnlyMatchesExact() {
            WindowPeriod zeroWindow = new WindowPeriod(0L, 0L);

            assertThat(zeroWindow.contains(targetDate, targetDate)).isTrue();
            assertThat(zeroWindow.contains(targetDate, new Date(targetDate.getTime() - DAY_MS))).isFalse();
            assertThat(zeroWindow.contains(targetDate, new Date(targetDate.getTime() + DAY_MS))).isFalse();
        }
    }

    // ---------------------------------------------------------------
    // Validation tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        /**
         * 验证 null beforeDays 被构造器拒绝。
         * 前置条件：使用 null 作为 beforeDays 参数构造 WindowPeriod。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "beforeDays must not be null"。
         */
        @Test
        @DisplayName("rejects null beforeDays")
        void rejectsNullBeforeDays() {
            assertThatThrownBy(() -> new WindowPeriod(null, 5L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("beforeDays must not be null");
        }

        /**
         * 验证 null afterDays 被构造器拒绝。
         * 前置条件：使用 null 作为 afterDays 参数构造 WindowPeriod。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "afterDays must not be null"。
         */
        @Test
        @DisplayName("rejects null afterDays")
        void rejectsNullAfterDays() {
            assertThatThrownBy(() -> new WindowPeriod(5L, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("afterDays must not be null");
        }

        /**
         * 验证负数 beforeDays 被构造器拒绝。
         * 前置条件：使用 -1L 作为 beforeDays 参数构造 WindowPeriod。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "beforeDays must be non-negative"。
         */
        @Test
        @DisplayName("rejects negative beforeDays")
        void rejectsNegativeBeforeDays() {
            assertThatThrownBy(() -> new WindowPeriod(-1L, 5L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("beforeDays must be non-negative");
        }

        /**
         * 验证负数 afterDays 被构造器拒绝。
         * 前置条件：使用 -1L 作为 afterDays 参数构造 WindowPeriod。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "afterDays must be non-negative"。
         */
        @Test
        @DisplayName("rejects negative afterDays")
        void rejectsNegativeAfterDays() {
            assertThatThrownBy(() -> new WindowPeriod(5L, -1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("afterDays must be non-negative");
        }

        /**
         * 验证 contains() 方法中 null targetDate 被拒绝。
         * 前置条件：调用 window.contains(null, targetDate)，传入 null 目标日期。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "targetDate must not be null"。
         */
        @Test
        @DisplayName("rejects null targetDate in contains")
        void rejectsNullTargetDate() {
            assertThatThrownBy(() -> window.contains(null, targetDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("targetDate must not be null");
        }

        /**
         * 验证 contains() 方法中 null date（待检查日期）被拒绝。
         * 前置条件：调用 window.contains(targetDate, null)，传入 null 待检查日期。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "date must not be null"。
         */
        @Test
        @DisplayName("rejects null date in contains")
        void rejectsNullDate() {
            assertThatThrownBy(() -> window.contains(targetDate, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("date must not be null");
        }
    }

    // ---------------------------------------------------------------
    // Equality tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Equality")
    class EqualityTests {

        /**
         * 验证相同 beforeDays 和 afterDays 的两个 WindowPeriod 对象相等且 hashCode 一致。
         * 前置条件：创建两个 WindowPeriod(beforeDays=3, afterDays=7) 对象。
         * 预期结果：两者 equals() 为 true，hashCode() 相同，符合值对象等价性约定。
         */
        @Test
        @DisplayName("same beforeDays and afterDays are equal")
        void sameValuesAreEqual() {
            WindowPeriod a = new WindowPeriod(3L, 7L);
            WindowPeriod b = new WindowPeriod(3L, 7L);

            assertThat(a)
                    .isEqualTo(b)
                    .hasSameHashCodeAs(b);
        }

        /**
         * 验证不同 beforeDays 的两个 WindowPeriod 对象不相等（afterDays 相同但 beforeDays 不同）。
         * 前置条件：创建 beforeDays 分别为 3 和 5、afterDays 均为 7 的两个对象。
         * 预期结果：equals() 返回 false，说明 beforeDays 参与等价性判断。
         */
        @Test
        @DisplayName("different beforeDays are not equal")
        void differentBeforeDaysNotEqual() {
            WindowPeriod a = new WindowPeriod(3L, 7L);
            WindowPeriod b = new WindowPeriod(5L, 7L);

            assertThat(a).isNotEqualTo(b);
        }

        /**
         * 验证不同 afterDays 的两个 WindowPeriod 对象不相等（beforeDays 相同但 afterDays 不同）。
         * 前置条件：创建 afterDays 分别为 7 和 10、beforeDays 均为 3 的两个对象。
         * 预期结果：equals() 返回 false，说明 afterDays 参与等价性判断。
         */
        @Test
        @DisplayName("different afterDays are not equal")
        void differentAfterDaysNotEqual() {
            WindowPeriod a = new WindowPeriod(3L, 7L);
            WindowPeriod b = new WindowPeriod(3L, 10L);

            assertThat(a).isNotEqualTo(b);
        }
    }

    // ---------------------------------------------------------------
    // Accessor tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Accessors")
    class AccessorTests {

        /**
         * 验证 getBeforeDays() 访问器返回构造时传入的 beforeDays 值。
         * 前置条件：创建 WindowPeriod(beforeDays=3, afterDays=7)。
         * 预期结果：getBeforeDays() 返回 3L，即前向窗口天数。
         */
        @Test
        @DisplayName("getBeforeDays returns the before-days value")
        void getBeforeDays() {
            assertThat(window.getBeforeDays()).isEqualTo(3L);
        }

        /**
         * 验证 getAfterDays() 访问器返回构造时传入的 afterDays 值。
         * 前置条件：创建 WindowPeriod(beforeDays=3, afterDays=7)。
         * 预期结果：getAfterDays() 返回 7L，即后向窗口天数。
         */
        @Test
        @DisplayName("getAfterDays returns the after-days value")
        void getAfterDays() {
            assertThat(window.getAfterDays()).isEqualTo(7L);
        }
    }
}
