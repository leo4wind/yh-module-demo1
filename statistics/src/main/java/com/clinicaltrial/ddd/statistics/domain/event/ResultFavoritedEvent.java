package com.clinicaltrial.ddd.statistics.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisResultId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ResultFavoritedEvent — 结果收藏事件.
 * <p>
 * 当分析结果被标记为收藏或取消收藏时触发。
 * 包含项目的ID、结果ID和收藏状态。
 * </p>
 */
public class ResultFavoritedEvent implements DomainEvent {

    private final AnalysisProjectId projectId;
    private final AnalysisResultId resultId;
    private final boolean favorited;
    private final LocalDateTime occurredOn;

    /**
     * 构造ResultFavoritedEvent.
     *
     * @param projectId  分析项目ID
     * @param resultId   分析结果ID
     * @param favorited  收藏状态（true为收藏，false为取消收藏）
     */
    public ResultFavoritedEvent(AnalysisProjectId projectId, AnalysisResultId resultId, boolean favorited) {
        this.projectId = projectId;
        this.resultId = resultId;
        this.favorited = favorited;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取分析项目ID.
     *
     * @return AnalysisProjectId
     */
    public AnalysisProjectId getProjectId() {
        return projectId;
    }

    /**
     * 获取分析结果ID.
     *
     * @return AnalysisResultId
     */
    public AnalysisResultId getResultId() {
        return resultId;
    }

    /**
     * 获取收藏状态.
     *
     * @return true 如果已收藏
     */
    public boolean isFavorited() {
        return favorited;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ResultFavorited: project " + projectId
                + ", result " + resultId
                + ", favorited=" + favorited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResultFavoritedEvent that = (ResultFavoritedEvent) o;
        return favorited == that.favorited
                && Objects.equals(projectId, that.projectId)
                && Objects.equals(resultId, that.resultId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, resultId, favorited, occurredOn);
    }

    @Override
    public String toString() {
        return "ResultFavoritedEvent{"
                + "projectId=" + projectId
                + ", resultId=" + resultId
                + ", favorited=" + favorited
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
