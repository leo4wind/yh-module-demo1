package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * AssessmentScore — 评估分数值对象.
 * <p>
 * 表示CRF评估的自动评分结果，包含：
 * <ul>
 *   <li>score — 评分值（字符串，允许各种评分格式如"85.5"、"A"等）</li>
 *   <li>result — 评分结果描述（如"normal"、"abnormal"等）</li>
 * </ul>
 * </p>
 */
public class AssessmentScore implements ValueObject {

    private final String score;
    private final String result;

    /**
     * 构造AssessmentScore.
     *
     * @param score  评分值，不能为空
     * @param result 评分结果描述，不能为空
     * @throws IllegalArgumentException 如果score或result为null
     */
    public AssessmentScore(String score, String result) {
        if (score == null) {
            throw new IllegalArgumentException("Score must not be null");
        }
        if (result == null) {
            throw new IllegalArgumentException("Result must not be null");
        }
        this.score = score;
        this.result = result;
    }

    /**
     * 获取评分值.
     *
     * @return 评分值字符串
     */
    public String getScore() {
        return score;
    }

    /**
     * 获取评分结果描述.
     *
     * @return 结果描述
     */
    public String getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssessmentScore that = (AssessmentScore) o;
        return Objects.equals(score, that.score) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, result);
    }

    @Override
    public String toString() {
        return "AssessmentScore{" + "score='" + score + '\'' + ", result='" + result + '\'' + '}';
    }
}
