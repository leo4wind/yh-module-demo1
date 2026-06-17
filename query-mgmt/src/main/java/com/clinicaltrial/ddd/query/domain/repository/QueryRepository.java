package com.clinicaltrial.ddd.query.domain.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;

import java.util.List;
import java.util.Optional;

/**
 * QueryRepository — 质疑仓储接口.
 * <p>
 * 提供Query聚合的持久化操作。
 * 遵循仓储模式，每个聚合对应一个仓储。
 * 仓储负责聚合的生命周期管理，包括加载、保存和删除。
 * </p>
 */
public interface QueryRepository {

    /**
     * 根据ID查找质疑.
     *
     * @param id 质疑ID
     * @return 包含Query的Optional
     */
    Optional<Query> findById(QueryId id);

    /**
     * 根据ID获取质疑，不存在时抛出异常.
     *
     * @param id 质疑ID
     * @return Query实例
     * @throws AggregateNotFoundException 如果未找到
     */
    default Query getById(QueryId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("Query", id));
    }

    /**
     * 保存质疑（新建或更新）.
     *
     * @param query Query实例
     * @return 保存后的Query实例
     */
    Query save(Query query);

    /**
     * 根据CRF评估ID查询所有质疑.
     *
     * @param assessmentId CRF评估ID
     * @return 质疑列表
     */
    List<Query> findByAssessmentId(CrfAssessmentId assessmentId);

    /**
     * 统计指定CRF评估下未关闭的质疑数量.
     * <p>
     * 未关闭的质疑包括OPEN和RESPONDED状态的质疑。
     * 用于判断CRF评估的所有质疑是否都已解决。
     * </p>
     *
     * @param assessmentId CRF评估ID
     * @return 未关闭的质疑数量
     */
    int countOpenQueriesByAssessmentId(CrfAssessmentId assessmentId);
}
