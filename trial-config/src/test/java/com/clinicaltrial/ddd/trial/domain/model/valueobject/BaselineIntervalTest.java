package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link BaselineInterval}.
 * <p>
 * Tests interval-to-days conversion, validation constraints, and value equality.
 * </p>
 */
class BaselineIntervalTest {

    // ---------------------------------------------------------------
    // Conversion tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Interval to days conversion")
    class ConversionTests {

        /**
         * 验证 DAY 单位的时间间隔转换逻辑：getIntervalInDays() 直接返回 interval 原值。
         * 前置条件：创建 BaselineInterval(5L, "DAY")，表示5天。
         * 预期结果：getIntervalInDays() 返回 5L，与 interval 值一致，即 DAY 单位无需换算。
         */
        @Test
        @DisplayName("DAY unit returns the interval value as-is")
        void dayUnitReturnsIntervalAsIs() {
            BaselineInterval interval = new BaselineInterval(5L, "DAY");
            assertThat(interval.getIntervalInDays()).isEqualTo(5L);
        }

        /**
         * 验证 WEEK 单位的时间间隔转换逻辑：getIntervalInDays() 返回 interval × 7。
         * 前置条件：创建 BaselineInterval(3L, "WEEK")，表示3周。
         * 预期结果：getIntervalInDays() 返回 21L（3周 × 7天/周），说明周单位按7天换算。
         */
        @Test
        @DisplayName("WEEK unit returns interval multiplied by 7")
        void weekUnitReturnsIntervalTimes7() {
            BaselineInterval interval = new BaselineInterval(3L, "WEEK");
            assertThat(interval.getIntervalInDays()).isEqualTo(21L);
        }

        /**
         * 验证 MONTH 单位的时间间隔转换逻辑：getIntervalInDays() 返回 interval × 30（近似值）。
         * 前置条件：创建 BaselineInterval(2L, "MONTH")，表示2个月。
         * 预期结果：getIntervalInDays() 返回 60L（2月 × 30天/月），说明月单位按30天近似换算。
         */
        @Test
        @DisplayName("MONTH unit returns interval multiplied by 30 (approximate)")
        void monthUnitReturnsIntervalTimes30() {
            BaselineInterval interval = new BaselineInterval(2L, "MONTH");
            assertThat(interval.getIntervalInDays()).isEqualTo(60L);
        }

        /**
         * 验证零值时间间隔的转换逻辑：当 interval 为0时无论单位如何，结果均为0。
         * 前置条件：创建 BaselineInterval(0L, "DAY")，表示0天。
         * 预期结果：getIntervalInDays() 返回 0L，说明零间隔的正确处理。
         */
        @Test
        @DisplayName("zero interval with DAY unit returns 0")
        void zeroIntervalReturnsZero() {
            BaselineInterval interval = new BaselineInterval(0L, "DAY");
            assertThat(interval.getIntervalInDays()).isEqualTo(0L);
        }

        /**
         * 验证大数值周单位的间隔换算仍然正确：52周 × 7天/周 = 364天。
         * 前置条件：创建 BaselineInterval(52L, "WEEK")，表示52周（约1年）。
         * 预期结果：getIntervalInDays() 返回 364L，确认大数值计算不会溢出或出错。
         */
        @Test
        @DisplayName("large interval with WEEK unit calculates correctly")
        void largeWeekInterval() {
            BaselineInterval interval = new BaselineInterval(52L, "WEEK");
            assertThat(interval.getIntervalInDays()).isEqualTo(364L);
        }

        /**
         * 验证单位字符串大小写不敏感：构造时无论传入 "day"、"DAY" 还是 "Day"，
         * 转换结果一致且对象相等（因为构造器内部对单位进行了大写标准化）。
         * 前置条件：创建三个 BaselineInterval 对象，值相同但单位大小写不同。
         * 预期结果：三个对象的 getIntervalInDays() 均返回 1L；三者 equals() 相等。
         */
        @Test
        @DisplayName("unit is case-insensitive (lowercase)")
        void unitIsCaseInsensitive() {
            BaselineInterval lower = new BaselineInterval(1L, "day");
            BaselineInterval upper = new BaselineInterval(1L, "DAY");
            BaselineInterval mixed = new BaselineInterval(1L, "Day");

            assertThat(lower.getIntervalInDays()).isEqualTo(1L);
            assertThat(upper.getIntervalInDays()).isEqualTo(1L);
            assertThat(mixed.getIntervalInDays()).isEqualTo(1L);
            assertThat(lower).isEqualTo(upper).isEqualTo(mixed);
        }
    }

    // ---------------------------------------------------------------
    // Validation tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        /**
         * 验证 null interval 被构造器拒绝。
         * 前置条件：使用 null 作为 interval 参数构造 BaselineInterval。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "interval must not be null"。
         */
        @Test
        @DisplayName("rejects null interval")
        void rejectsNullInterval() {
            assertThatThrownBy(() -> new BaselineInterval(null, "DAY"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("interval must not be null");
        }

        /**
         * 验证 null unit 被构造器拒绝。
         * 前置条件：使用 null 作为 unit 参数构造 BaselineInterval。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "unit must not be null or empty"。
         */
        @Test
        @DisplayName("rejects null unit")
        void rejectsNullUnit() {
            assertThatThrownBy(() -> new BaselineInterval(1L, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unit must not be null or empty");
        }

        /**
         * 验证空字符串 unit 被构造器拒绝。
         * 前置条件：使用空字符串 "" 作为 unit 参数构造 BaselineInterval。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "unit must not be null or empty"。
         */
        @Test
        @DisplayName("rejects empty unit")
        void rejectsEmptyUnit() {
            assertThatThrownBy(() -> new BaselineInterval(1L, ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unit must not be null or empty");
        }

        /**
         * 验证空白字符串 unit（仅包含空格）被构造器拒绝。
         * 前置条件：使用空白字符串 "  " 作为 unit 参数构造 BaselineInterval。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "unit must not be null or empty"。
         */
        @Test
        @DisplayName("rejects blank unit")
        void rejectsBlankUnit() {
            assertThatThrownBy(() -> new BaselineInterval(1L, "  "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unit must not be null or empty");
        }

        /**
         * 验证非法的单位字符串（非 DAY/WEEK/MONTH）被构造器拒绝。
         * 前置条件：使用 "YEAR" 作为 unit 参数构造 BaselineInterval。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "unit must be DAY, WEEK, or MONTH"。
         */
        @Test
        @DisplayName("rejects invalid unit string")
        void rejectsInvalidUnit() {
            assertThatThrownBy(() -> new BaselineInterval(1L, "YEAR"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unit must be DAY, WEEK, or MONTH");
        }

        /**
         * 验证负数 interval 被构造器拒绝。
         * 前置条件：使用 -1L 作为 interval 参数构造 BaselineInterval。
         * 预期结果：抛出 IllegalArgumentException，异常消息包含 "interval must be non-negative"。
         */
        @Test
        @DisplayName("rejects negative interval")
        void rejectsNegativeInterval() {
            assertThatThrownBy(() -> new BaselineInterval(-1L, "DAY"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("interval must be non-negative");
        }
    }

    // ---------------------------------------------------------------
    // Equality tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Equality")
    class EqualityTests {

        /**
         * 验证相同 interval 和 unit 的两个 BaselineInterval 对象相等且 hashCode 一致。
         * 前置条件：创建两个 interval=3L、unit="DAY" 的 BaselineInterval 对象。
         * 预期结果：两者 equals() 为 true，hashCode() 相同，符合值对象等价性约定。
         */
        @Test
        @DisplayName("same interval and unit are equal")
        void sameIntervalAndUnitAreEqual() {
            BaselineInterval a = new BaselineInterval(3L, "DAY");
            BaselineInterval b = new BaselineInterval(3L, "DAY");

            assertThat(a)
                    .isEqualTo(b)
                    .hasSameHashCodeAs(b);
        }

        /**
         * 验证不同 interval 的两个 BaselineInterval 对象不相等（unit 相同但 interval 不同）。
         * 前置条件：创建 interval 分别为 3L 和 5L，unit 均为 "DAY" 的两个对象。
         * 预期结果：equals() 返回 false，说明 interval 参与等价性判断。
         */
        @Test
        @DisplayName("different interval are not equal")
        void differentIntervalNotEqual() {
            BaselineInterval a = new BaselineInterval(3L, "DAY");
            BaselineInterval b = new BaselineInterval(5L, "DAY");

            assertThat(a).isNotEqualTo(b);
        }

        /**
         * 验证不同 unit 的两个 BaselineInterval 对象不相等（interval 相同但 unit 不同）。
         * 前置条件：创建 interval 均为 3L，unit 分别为 "DAY" 和 "WEEK" 的两个对象。
         * 预期结果：equals() 返回 false，说明 unit 参与等价性判断。
         */
        @Test
        @DisplayName("different unit are not equal")
        void differentUnitNotEqual() {
            BaselineInterval a = new BaselineInterval(3L, "DAY");
            BaselineInterval b = new BaselineInterval(3L, "WEEK");

            assertThat(a).isNotEqualTo(b);
        }

        /**
         * 验证单位字符串大小写标准化后，大小写不同的值对象仍然相等。
         * 前置条件：创建两个 BaselineInterval，interval 均为 3L，unit 分别为 "day"（小写）和 "DAY"（大写）。
         * 预期结果：两者 equals() 为 true 且 hashCode() 相同，因为构造器将 unit 统一转为大写。
         */
        @Test
        @DisplayName("normalized unit makes case-different values equal")
        void normalizedUnitMakesCaseDifferentEqual() {
            BaselineInterval a = new BaselineInterval(3L, "day");
            BaselineInterval b = new BaselineInterval(3L, "DAY");

            assertThat(a)
                    .isEqualTo(b)
                    .hasSameHashCodeAs(b);
        }
    }

    // ---------------------------------------------------------------
    // Accessor tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Accessors")
    class AccessorTests {

        /**
         * 验证 getInterval() 访问器返回原始的 interval 值（未经单位换算）。
         * 前置条件：创建 BaselineInterval(7L, "DAY")。
         * 预期结果：getInterval() 返回 7L，即构造时传入的原始数值。
         */
        @Test
        @DisplayName("getInterval returns the raw interval value")
        void getInterval() {
            BaselineInterval interval = new BaselineInterval(7L, "DAY");
            assertThat(interval.getInterval()).isEqualTo(7L);
        }

        /**
         * 验证 getUnit() 访问器返回标准化为大写的单位字符串。
         * 前置条件：使用小写 "month" 构造 BaselineInterval(1L, "month")。
         * 预期结果：getUnit() 返回 "MONTH"（大写），说明构造器内部对单位进行了大写标准化。
         */
        @Test
        @DisplayName("getUnit returns the normalized uppercase unit")
        void getUnit() {
            BaselineInterval interval = new BaselineInterval(1L, "month");
            assertThat(interval.getUnit()).isEqualTo("MONTH");
        }
    }
}
